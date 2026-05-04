package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;

public interface CollectService {
    String addCollect(String userId, String videoId) throws ServerException;

    Boolean exists(String userId, String videoId) throws ServerException;

    void deleteCollect(String userId, String videoId) throws ServerException;
}
