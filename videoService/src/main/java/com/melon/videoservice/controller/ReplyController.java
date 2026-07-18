package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.dto.ReplyDto;
import com.melon.videoservice.pojo.vo.ReplyVo;
import com.melon.videoservice.service.ReplyService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reply")
public class ReplyController {
    @Resource
    private ReplyService replyService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public ReplyVo addReply(@RequestBody @Validated ReplyDto replyDto) throws ServerException {
        if (!replyDto.getType().equals("c") && !replyDto.getType().equals("r")) {
            throw new ServerException("The type of reply is not correct");
        }
        return replyService.addReply(replyDto.getUserId(),
                replyDto.getType(),
                replyDto.getTargetId(),
                replyDto.getCommentId(),
                replyDto.getContent());
    }

    @DeleteMapping("/{replyId}")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deleteReply(@PathVariable String replyId) throws ServerException {
        replyService.deleteReplyByReplyId(replyId);
    }
}
