package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.util.SecurityUtils;
import com.melon.videoservice.pojo.dto.CommentDto;
import com.melon.videoservice.pojo.vo.CommentVo;
import com.melon.videoservice.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public CommentVo addComment(@RequestBody @Validated CommentDto commentDto) throws ServerException {
        return commentService.addComment(SecurityUtils.getCurrentUserId(),
                commentDto.getVideoId(),
                commentDto.getContent());
    }

    @GetMapping("/list")
    public List<CommentVo> getCommentListByVideoId(@RequestParam("videoId") String videoId) throws ServerException {
        return commentService.getCommentListByUserIdAndVideoId(SecurityUtils.getCurrentUserIdOrNull(), videoId);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deleteComment(@RequestBody Map<String, String> body) throws ServerException {
        if (body.containsKey("commentId")) {
            commentService.deleteCommentByCommentId(body.get("commentId"), SecurityUtils.getCurrentUserId());
        } else {
            throw new ServerException("Please enter a valid commentId");
        }
    }
}
