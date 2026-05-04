package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;

public interface LikeService {
    String addLike(String userId, String videoId) throws ServerException;

    Boolean exists(String userId, String videoId) throws ServerException;

    void deleteLike(String userId, String videoId) throws ServerException;

    long getLikeCount(String videoId) throws ServerException;
}
