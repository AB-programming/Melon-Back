package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;

public interface CommentLikeService {
    String addCommentLike(String userId, String commentId) throws ServerException;

    void deleteCommentLike(String userId, String commentId) throws ServerException;

    Boolean exists(String userId, String commentId);
}