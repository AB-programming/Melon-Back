package com.melon.videoservice.service;

import com.melon.baseservice.service.HdfsService;
import com.melon.videoservice.pojo.message.DeleteVideoMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "video-delete-topic", consumerGroup = "video-consumer-group")
public class DeleteVideoConsumer implements RocketMQListener<DeleteVideoMessage> {
    @Resource
    private HdfsService hdfsService;

    @Override
    public void onMessage(DeleteVideoMessage deleteVideoMessage) {
        hdfsService.delete(deleteVideoMessage.getVideoPath(), true);
        hdfsService.delete(deleteVideoMessage.getCoverPath(), true);
    }
}
