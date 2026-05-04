package com.melon.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.entity.User;
import com.melon.userservice.mapper.SubscriptionMapper;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.entity.Subscription;
import com.melon.userservice.service.SubscriptionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SubscriptionMapper subscriptionMapper;

    @Override
    public String addSubscription(String userId, String targetId) throws ServerException {
        User target = userMapper.selectById(targetId);
        if (Objects.isNull(target)) {
            throw new ServerException("User does not exist");
        }
        if (this.isSubscribed(userId, targetId)) {
            throw new ServerException("Subscription already exists");
        }
        String id = UUID.randomUUID().toString();
        Subscription subscription = Subscription.builder()
                .id(id)
                .subscriber(userId)
                .target(targetId)
                .build();
        if (subscriptionMapper.insert(subscription) <= 0) {
            throw new ServerException("Insert failed, please try again later");
        }
        return id;
    }

    @Override
    public Boolean isSubscribed(String userId, String targetId) {
        LambdaQueryWrapper<Subscription> subscriptionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        subscriptionLambdaQueryWrapper.allEq(Map.of(Subscription::getSubscriber, userId, Subscription::getTarget, targetId));
        return subscriptionMapper.exists(subscriptionLambdaQueryWrapper);
    }

    @Override
    public void deleteSubscription(String userId, String targetId) throws ServerException {
        User target = userMapper.selectById(targetId);
        if (Objects.isNull(target)) {
            throw new ServerException("User does not exist");
        }
        if (this.isSubscribed(userId, targetId)) {
            LambdaQueryWrapper<Subscription> subscriptionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            subscriptionLambdaQueryWrapper.allEq(Map.of(Subscription::getSubscriber, userId, Subscription::getTarget, targetId));
            subscriptionMapper.delete(subscriptionLambdaQueryWrapper);
        }
    }

    @Override
    public Long getSubscriptionCount(String userId) {
        LambdaQueryWrapper<Subscription> subscriptionLambdaQueryWrapper = new LambdaQueryWrapper<Subscription>().eq(Subscription::getTarget, userId);
        return subscriptionMapper.selectCount(subscriptionLambdaQueryWrapper);
    }
}
