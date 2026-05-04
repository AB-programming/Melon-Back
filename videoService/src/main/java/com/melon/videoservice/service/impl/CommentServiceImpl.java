package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.CommentLikeMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.CommentLike;
import com.melon.videoservice.pojo.vo.CommentVo;
import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private UserRemote userRemote;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CommentVo addComment(String userId, String videoId, String content) throws ServerException {
        String id = UUID.randomUUID().toString();
        Comment comment = Comment.builder()
                .id(id)
                .userId(userId)
                .videoId(videoId)
                .content(content)
                .createdTime(LocalDateTime.now())
                .build();
        if (commentMapper.insert(comment) <= 0) {
            throw new ServerException("Add comment failed, please try again");
        }
        HttpResult<UserVo> result = userRemote.getUserById(userId);
        return CommentVo.builder()
                .id(id)
                .user(result.getData())
                .content(content)
                .isLiked(false)
                .likeCount(0L)
                .createdTime(comment.getCreatedTime().format(formatter))
                .build();
    }

    @Override
    public List<CommentVo> getCommentListByUserIdAndVideoId(String userId, String videoId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper =
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getVideoId, videoId)
                        .orderByDesc(Comment::getCreatedTime);
        List<Comment> commentList = commentMapper.selectList(commentLambdaQueryWrapper);
        return commentList.parallelStream()
                .map(comment -> {
                    Long likeCount = commentLikeMapper.selectCount(new LambdaQueryWrapper<CommentLike>()
                            .eq(CommentLike::getCommentId, comment.getId()));
                    boolean isLiked = false;
                    if (Objects.nonNull(userId) && !userId.isEmpty()) {
                        isLiked = commentLikeMapper.exists(new LambdaQueryWrapper<CommentLike>()
                                .allEq(Map.of(CommentLike::getUserId, userId, CommentLike::getCommentId, comment.getId())));
                    }
                    CommentVo.CommentVoBuilder builder = CommentVo.builder();
                    builder.id(comment.getId())
                            .content(comment.getContent())
                            .likeCount(likeCount)
                            .isLiked(isLiked)
                            .createdTime(comment.getCreatedTime().format(formatter));
                    HttpResult<UserVo> result = userRemote.getUserById(comment.getUserId());
                    if (result.getCode().equals(HttpResponseStatus.OK.getCode())) {
                        builder.user(result.getData());
                    }
                    return builder.build();
                }).toList();
    }

    @Override
    public void deleteCommentByCommentId(String commentId) throws ServerException {
        if (commentMapper.deleteById(commentId) <= 0) {
            throw new ServerException("Failed to delete the comment, please try again later");
        }
    }
}
