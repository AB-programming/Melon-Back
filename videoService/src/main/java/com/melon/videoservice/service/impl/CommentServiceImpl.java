package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.CommentLikeMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.CommentLike;
import com.melon.videoservice.pojo.entity.Reply;
import com.melon.videoservice.pojo.vo.CommentVo;
import com.melon.videoservice.pojo.vo.ReplyVo;
import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private UserRemote userRemote;

    @Resource
    private ReplyMapper replyMapper;

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
    public List<CommentVo> getCommentListByUserIdAndVideoId(String userId, String videoId) throws ServerException {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper =
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getVideoId, videoId)
                        .orderByDesc(Comment::getCreatedTime);
        List<Comment> commentList = commentMapper.selectList(commentLambdaQueryWrapper);
        List<String> commentUserIds = commentList.stream()
                .map(Comment::getUserId)
                .toList();
        List<String> commentIds = commentList.stream()
                .map(Comment::getId)
                .toList();
        List<Reply> replyList = Collections.emptyList();
        if (!CollectionUtils.isEmpty(commentIds)) {
            replyList = replyMapper.selectList(new LambdaQueryWrapper<Reply>().in(Reply::getCommentId, commentIds));
        }
        List<String> replyUserIds = replyList.stream()
                .map(Reply::getUserId)
                .toList();
        List<String> replyTargetUserIds = replyList.stream()
                .map(Reply::getTargetUserId)
                .toList();
        List<String> userIds = Stream.of(commentUserIds, replyUserIds, replyTargetUserIds)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        HttpResult<Map<String, UserVo>> userMapResult = userRemote.getUserListByIds(userIds);
        if (!userMapResult.getCode().equals(HttpResponseStatus.OK.getCode())) {
            throw new ServerException("user module exception");
        }
        Map<String, UserVo> userMap = userMapResult.getData();
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
                    UserVo userVo = userMap.get(comment.getUserId());
                    if (Objects.nonNull(userVo)) {
                        builder.user(userVo);
                    }
                    // query replies under this comment
                    List<Reply> replies = replyMapper.selectList(new LambdaQueryWrapper<Reply>().eq(Reply::getCommentId, comment.getId()));
                    List<ReplyVo> replyVoList = replies.stream().map(reply -> {
                        ReplyVo.ReplyVoBuilder replyVoBuilder = ReplyVo.builder();
                        replyVoBuilder.id(reply.getId())
                                .type(reply.getType())
                                .user(userMap.get(reply.getUserId()))
                                .targetId(reply.getTargetId())
                                .targetUser(userMap.get(reply.getTargetUserId()))
                                .content(reply.getContent())
                                .createdTime(reply.getCreatedTime().format(formatter));
                        return replyVoBuilder.build();
                    }).toList();
                    builder.replyList(replyVoList);
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
