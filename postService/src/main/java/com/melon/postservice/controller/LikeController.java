package com.melon.postservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.postservice.pojo.dto.PostLikeDto;
import com.melon.postservice.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeController {
    @Resource
    private LikeService likeService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String addPostLike(@RequestBody @Validated PostLikeDto postLikeDto) throws ServerException {
        return likeService.addLike(postLikeDto.getUserId(), postLikeDto.getPostId());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deletePostLike(@RequestBody @Validated PostLikeDto postLikeDto) {
        likeService.deleteLike(postLikeDto.getUserId(), postLikeDto.getPostId());
    }
}
