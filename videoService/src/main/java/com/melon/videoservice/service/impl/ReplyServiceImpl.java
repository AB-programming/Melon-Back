package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.Reply;
import com.melon.videoservice.pojo.vo.ReplyVo;
import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.ReplyService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReplyServiceImpl implements ReplyService {
    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private UserRemote userRemote;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public ReplyVo addReply(String userId, String type, String targetId, String commentId, String content) throws ServerException {
        String id = UUID.randomUUID().toString();
        LocalDateTime dateTime = LocalDateTime.now();
        String targetUserId = "";
        if (type.equals("c")) {
            // reply a comment
            Comment comment = commentMapper.selectById(targetId);
            if (Objects.nonNull(comment)) {
                targetUserId = comment.getUserId();
            }
        } else {
            // reply a reply
            Reply reply = replyMapper.selectById(targetId);
            if (Objects.nonNull(reply)) {
                targetUserId = reply.getUserId();
            }
        }
        Reply reply = Reply.builder().id(id)
                .userId(userId)
                .commentId(commentId)
                .content(content)
                .type(type)
                .targetId(targetId)
                .targetUserId(targetUserId)
                .createdTime(dateTime)
                .build();
        if (replyMapper.insert(reply) <= 0) {
            throw new ServerException("Reply failed, please try again");
        }
        HttpResult<UserVo> userResult = userRemote.getUserById(userId);
        HttpResult<UserVo> targetUserResult = userRemote.getUserById(targetUserId);
        return ReplyVo.builder()
                .id(id)
                .user(userResult.getData())
                .type(type)
                .content(content)
                .targetId(targetId)
                .targetUser(targetUserResult.getData())
                .createdTime(dateTime.format(formatter))
                .build();
    }

    @Override
    public Boolean exists(String userId, String type, String targetId) {
        LambdaQueryWrapper<Reply> replyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        replyLambdaQueryWrapper.allEq(Map.of(Reply::getUserId, userId, Reply::getTargetId, targetId, Reply::getType, type));
        return replyMapper.exists(replyLambdaQueryWrapper);
    }
}
