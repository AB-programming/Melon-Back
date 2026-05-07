package com.melon.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.entity.User;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.mapper.SubscriptionMapper;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.entity.Subscription;
import com.melon.userservice.service.SubscriptionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<UserVo> getMySubscriptions(String subscriber) {
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<Subscription>()
                .eq(Subscription::getSubscriber, subscriber);
        List<Subscription> subscriptions = subscriptionMapper.selectList(wrapper);
        return subscriptions.stream().map(sub -> {
            User user = userMapper.selectById(sub.getTarget());
            if (user == null) {
                return null;
            }
            return UserVo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatarUrl(user.getAvatarUrl())
                    .signature(user.getSignature())
                    .introduction(user.getIntroduction())
                    .residence(user.getResidence())
                    .interest(user.getInterest())
                    .gender(user.getGender())
                    .build();
        }).filter(Objects::nonNull).toList();
    }
}