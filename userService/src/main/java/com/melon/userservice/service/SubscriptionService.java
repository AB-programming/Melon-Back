package com.melon.userservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;

import java.util.List;

public interface SubscriptionService {
    String addSubscription(String userId, String targetId) throws ServerException;

    Boolean isSubscribed(String userId, String targetId);

    void deleteSubscription(String userId, String targetId) throws ServerException;

    Long getSubscriptionCount(String userId);

    List<UserVo> getMySubscriptions(String subscriber);
}