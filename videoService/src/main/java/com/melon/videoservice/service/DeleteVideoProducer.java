package com.melon.videoservice.service;

import com.melon.videoservice.pojo.message.DeleteVideoMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeleteVideoProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendDeleteMessage(String videoId, String videoPath, String coverPath) {
        DeleteVideoMessage message = DeleteVideoMessage.builder()
                .videoId(videoId)
                .videoPath(videoPath)
                .coverPath(coverPath)
                .build();
        rocketMQTemplate.syncSend("video-delete-topic", message);
    }
}
