package com.melon.postservice.service;

import com.melon.commonservice.exception.ServerException;

public interface LikeService {
    String addLike(String userId, String postId) throws ServerException;
    Boolean exists(String userId, String postId);
    void deleteLike(String userId, String postId);
}
