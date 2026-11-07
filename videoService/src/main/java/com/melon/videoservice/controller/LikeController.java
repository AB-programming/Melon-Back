package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.util.SecurityUtils;
import com.melon.videoservice.pojo.dto.LikeDto;
import com.melon.videoservice.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeController {
    @Resource
    private LikeService likeService;

    @GetMapping("/isLike")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Boolean isLike(@RequestParam("videoId") String videoId) throws ServerException {
        return likeService.exists(SecurityUtils.getCurrentUserId(), videoId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String addLike(@RequestBody @Validated LikeDto likeDto) throws ServerException {
        return likeService.addLike(SecurityUtils.getCurrentUserId(), likeDto.getVideoId());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deleteLike(@RequestBody @Validated LikeDto likeDto) throws ServerException {
        likeService.deleteLike(SecurityUtils.getCurrentUserId(), likeDto.getVideoId());
    }

    @GetMapping("/count")
    public long getVideoCount(@RequestParam("videoId") String videoId) throws ServerException {
        return likeService.getLikeCount(videoId);
    }
}
