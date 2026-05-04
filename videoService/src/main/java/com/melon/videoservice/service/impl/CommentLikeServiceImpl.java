package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.CommentLikeMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.CommentLike;
import com.melon.videoservice.service.CommentLikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {
    @Resource
    private CommentLikeMapper commentLikemapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    public String addCommentLike(String userId, String commentId) throws ServerException {
        Comment comment = commentMapper.selectById(commentId);
        if (Objects.isNull(comment)) {
            throw new ServerException("The comment is not exists!");
        }
        if (this.exists(userId, commentId)) {
            throw new ServerException("The user already like this comment!");
        }
        String id = UUID.randomUUID().toString();
        CommentLike commentLike = CommentLike.builder()
                .id(id)
                .userId(userId)
                .commentId(commentId)
                .build();
        if (commentLikemapper.insert(commentLike) <= 0) {
            throw new ServerException("Failed to like the comment, please try again later");
        }
        return id;
    }

    @Override
    public void deleteCommentLike(String userId, String commentId) throws ServerException {
        LambdaQueryWrapper<CommentLike> commentLikeLambdaQueryWrapper = new LambdaQueryWrapper<CommentLike>()
                .allEq(Map.of(CommentLike::getUserId, userId, CommentLike::getCommentId, commentId));
        if (commentLikemapper.delete(commentLikeLambdaQueryWrapper) <= 0) {
            throw new ServerException("Failed to delete the comment, please try again later");
        }
    }

    @Override
    public Boolean exists(String userId, String commentId) {
        LambdaQueryWrapper<CommentLike> commentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLikeLambdaQueryWrapper.allEq(Map.of(CommentLike::getUserId, userId, CommentLike::getCommentId, commentId));
        return commentLikemapper.exists(commentLikeLambdaQueryWrapper);
    }
}
