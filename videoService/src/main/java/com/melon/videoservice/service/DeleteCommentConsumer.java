package com.melon.videoservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Reply;
import com.melon.videoservice.pojo.message.DeleteCommentMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "comment-delete-topic", consumerGroup = "comment-consumer-group")
@Slf4j
public class DeleteCommentConsumer implements RocketMQListener<DeleteCommentMessage> {
    @Resource
    private ReplyMapper replyMapper;

    @Override
    public void onMessage(DeleteCommentMessage message) {
        if (replyMapper.delete(new LambdaQueryWrapper<Reply>()
                .eq(Reply::getCommentId, message.getCommentId())) <= 0) {
            log.error("Delete replies failed for commentId={}", message.getCommentId());
        }
    }
}
