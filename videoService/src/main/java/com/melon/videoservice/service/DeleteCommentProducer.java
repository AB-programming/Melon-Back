package com.melon.videoservice.service;

import com.melon.videoservice.pojo.message.DeleteCommentMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeleteCommentProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendDeleteCommentMessage(String commentId) {
        DeleteCommentMessage message = DeleteCommentMessage.builder()
                .commentId(commentId)
                .build();
        rocketMQTemplate.syncSend("comment-delete-topic", message);
    }
}
