package com.melon.userservice.controller;

import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.commonservice.exception.ServerException;
import com.melon.userservice.pojo.dto.SubscriptionDto;
import com.melon.userservice.service.SubscriptionService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    @Resource
    private SubscriptionService subscriptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String addSubscription(@RequestBody @Validated SubscriptionDto subscriptionDto) throws ServerException {
        return subscriptionService.addSubscription(subscriptionDto.getSubscriber(), subscriptionDto.getTarget());
    }

    @GetMapping("/isSubscribed")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Boolean isSubscribed(@RequestParam("subscriber") String subscriber, @RequestParam("targetId") String targetId) {
        return subscriptionService.isSubscribed(subscriber, targetId);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void cancelSubscription(@RequestBody @Validated SubscriptionDto subscriptionDto) throws ServerException {
        subscriptionService.deleteSubscription(subscriptionDto.getSubscriber(), subscriptionDto.getTarget());
    }

    @GetMapping("/getFans")
    public Long getFans(@RequestParam("userId") String userId) {
        return subscriptionService.getSubscriptionCount(userId);
    }

    @GetMapping("/getMySubscriptions")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public List<UserVo> getMySubscriptions(@RequestParam("subscriber") String subscriber) {
        return subscriptionService.getMySubscriptions(subscriber);
    }
}