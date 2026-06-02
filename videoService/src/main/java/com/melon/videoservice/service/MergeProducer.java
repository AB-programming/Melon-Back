package com.melon.videoservice.service;

import com.melon.videoservice.pojo.message.MergeMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service
public class MergeProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMergeMessage(String fileMd5, String fileId) {
        MergeMessage message = MergeMessage.builder()
                .fileMd5(fileMd5)
                .fileId(fileId)
                .build();
        rocketMQTemplate.syncSend("video-merge-topic", message);
    }
}
