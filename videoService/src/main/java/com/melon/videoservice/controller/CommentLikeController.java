package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.dto.CommentLikeDto;
import com.melon.videoservice.service.CommentLikeService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commentLike")
public class CommentLikeController {
    @Resource
    private CommentLikeService commentLikeService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String addCommentLike(@RequestBody @Validated CommentLikeDto commentLikeDto) throws ServerException {
        return commentLikeService.addCommentLike(commentLikeDto.getUserId(), commentLikeDto.getCommentId());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void cancelCommentLike(@RequestBody @Validated CommentLikeDto commentLikeDto) throws ServerException {
        commentLikeService.deleteCommentLike(commentLikeDto.getUserId(), commentLikeDto.getCommentId());
    }
}
