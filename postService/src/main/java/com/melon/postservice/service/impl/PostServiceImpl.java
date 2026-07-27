package com.melon.postservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.baseservice.service.HdfsService;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.postservice.mapper.LikeMapper;
import com.melon.postservice.mapper.PostMapper;
import com.melon.postservice.pojo.entity.Post;
import com.melon.postservice.pojo.entity.PostLike;
import com.melon.postservice.pojo.vo.PostVo;
import com.melon.postservice.remote.UserRemote;
import com.melon.postservice.service.PostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Resource
    private PostMapper postMapper;

    @Resource
    private LikeMapper postLikeMapper;

    @Resource
    private UserRemote userRemote;

    @Resource
    private HdfsService hdfsService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PostVo addPost(String userId, String content, MultipartFile[] files) throws ServerException {
        HttpResult<UserVo> userResult = userRemote.getUserById(userId);
        if (!userResult.getCode().equals(HttpResponseStatus.OK.getCode())) {
            throw new ServerException("The user is not exist");
        }
        String id = UUID.randomUUID().toString();
        List<String> imageList = new ArrayList<>();
        StringBuilder images = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            String imageExtension = StringUtils.getFilenameExtension(files[i].getOriginalFilename());
            String image = id + '-' + i + '.' + imageExtension;
            String imageUrl = "/post/" + image;
            imageList.add(image);
            images.append(image);
            if (i < files.length - 1) {
                images.append(";");
            }
            try {
                hdfsService.upload(imageUrl, files[i].getInputStream());
            } catch (IOException e) {
                throw new ServerException("Failed to upload the video.");
            }
        }
        LocalDateTime dateTime = LocalDateTime.now();
        Post post = Post.builder()
                .id(id)
                .userId(userId)
                .content(content)
                .images(images.toString())
                .createdTime(dateTime)
                .build();
        if (postMapper.insert(post) <= 0) {
            throw new ServerException("Failed to add the post.");
        }
        return PostVo.builder()
                .id(id)
                .user(userResult.getData())
                .content(content)
                .images(imageList)
                .isLike(false)
                .likeCount(0L)
                .createdTime(dateTime.format(formatter))
                .build();
    }

    @Override
    public StreamingResponseBody getPostImage(String image, HttpServletResponse response) throws ServerException {
        String extension = StringUtils.getFilenameExtension(image);
        String imageUrl = "/post/" + image;
        long length = hdfsService.getLength(imageUrl);
        response.setContentLengthLong(length);
        if (Objects.isNull(extension)) {
            throw new ServerException("The picture path does not exist.");
        }
        switch (extension) {
            case "jpg", "jpeg" -> response.setContentType("image/jpeg");
            case "png" -> response.setContentType("image/png");
            case "gif" -> response.setContentType("image/gif");
            case "webp" -> response.setContentType("image/webp");
        }
        return outputStream -> {
            try (FSDataInputStream in = hdfsService.open(imageUrl)) {
                IOUtils.copyBytes(in, outputStream, 8 * 1024, false);
            }
        };
    }

    @Override
    public List<PostVo> selectAllPost() {
        return selectPostList("");
    }

    @Override
    public List<PostVo> selectPostListWithUserId(String userId) {
        return selectPostList(userId);
    }

    @Override
    public List<PostVo> selectFollowedPosts(String userId) throws ServerException {
        HttpResult<List<UserVo>> userResult = userRemote.getMySubscriptions(userId);
        if (HttpResponseStatus.OK.getCode() != userResult.getCode()) {
            throw new ServerException("Failed to fetch subscriptions.");
        }
        List<UserVo> followedUsers = userResult.getData();
        List<String> followedIds = followedUsers.stream().map(UserVo::getId).toList();
        if (followedIds.isEmpty()) {
            return Collections.emptyList();
        }
        // user list convert to user map
        Map<String, UserVo> userMap = followedUsers.stream()
                .collect(Collectors.toMap(UserVo::getId, Function.identity()));

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Post::getUserId, followedIds).orderByDesc(Post::getCreatedTime);
        return postMapper.selectList(wrapper)
                .stream()
                .map(post -> {
                    PostVo.PostVoBuilder builder = PostVo.builder();
                    UserVo user = userMap.get(post.getUserId());
                    if (Objects.nonNull(user)) {
                        builder.user(user);
                    }
                    builder.isLike(postLikeMapper.exists(new LambdaQueryWrapper<PostLike>()
                            .allEq(Map.of(PostLike::getUserId, userId, PostLike::getPostId, post.getId()))));
                    Long likeCount = postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, post.getId()));
                    return builder
                            .id(post.getId())
                            .content(post.getContent())
                            .images(Arrays.stream(post.getImages().split(";")).toList())
                            .createdTime(post.getCreatedTime().format(formatter))
                            .likeCount(likeCount)
                            .build();
                }).toList();
    }

    @Override
    public void deletePost(String postId) throws ServerException {
        Post post = postMapper.selectById(postId);
        if (Objects.isNull(post)) {
            return;
        }
        if (postMapper.deleteById(postId) <= 0) {
            throw new ServerException("Failed to delete the post.");
        }
        postLikeMapper.delete(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, postId));
        // Delete post image from hdfs
        String[] images = post.getImages().split(";");
        for (String image : images) {
            hdfsService.delete("/post/" + image, true);
        }
    }

    @Override
    public PostVo getPostById(String postId) throws ServerException {
        return getPost(postId, "");
    }

    @Override
    public PostVo getPostByIdWithUserId(String postId, String userId) throws ServerException {
        return getPost(postId, userId);
    }

    private PostVo getPost(String postId, String userId) throws ServerException {
        Post post = postMapper.selectById(postId);
        if (Objects.isNull(post)) {
            throw new ServerException("The post does not exist.");
        }
        PostVo.PostVoBuilder builder = PostVo.builder();
        HttpResult<UserVo> result = userRemote.getUserById(post.getUserId());
        if (result.getCode().equals(HttpResponseStatus.OK.getCode())) {
            builder.user(result.getData());
        }
        if (userId.isEmpty()) {
            builder.isLike(false);
        } else {
            builder.isLike(postLikeMapper.exists(new LambdaQueryWrapper<PostLike>()
                    .allEq(Map.of(PostLike::getUserId, userId, PostLike::getPostId, postId))));
        }
        Long likeCount = postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, postId));
        return builder.id(post.getId())
                .content(post.getContent())
                .images(Arrays.stream(post.getImages().split(";")).toList())
                .likeCount(likeCount)
                .createdTime(post.getCreatedTime().format(formatter))
                .build();
    }

    /**
     * selectPostList by userId, userId can be empty
     */
    private List<PostVo> selectPostList(String userId) {
        LambdaQueryWrapper<Post> postLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLambdaQueryWrapper.orderByDesc(Post::getCreatedTime);
        return postMapper.selectList(postLambdaQueryWrapper)
                .stream()
                .map(post -> {
                    PostVo.PostVoBuilder builder = PostVo.builder();
                    HttpResult<UserVo> userResult = userRemote.getUserById(post.getUserId());
                    if (userResult.getCode().equals(HttpResponseStatus.OK.getCode())) {
                        builder.user(userResult.getData());
                    }
                    if (!userId.isEmpty()) {
                        builder.isLike(postLikeMapper.exists(new LambdaQueryWrapper<PostLike>().allEq(Map.of(PostLike::getUserId, userId,
                                PostLike::getPostId, post.getId()))));
                    } else {
                        builder.isLike(false);
                    }
                    Long likeCount = postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, post.getId()));
                    return builder
                            .id(post.getId())
                            .content(post.getContent())
                            .images(Arrays.stream(post.getImages().split(";")).toList())
                            .createdTime(post.getCreatedTime().format(formatter))
                            .likeCount(likeCount)
                            .build();
                }).toList();
    }
}
