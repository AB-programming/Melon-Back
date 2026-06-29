package com.melon.videoservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.baseservice.service.HdfsService;
import com.melon.videoservice.mapper.CollectMapper;
import com.melon.videoservice.mapper.LikeMapper;
import com.melon.videoservice.pojo.entity.Collect;
import com.melon.videoservice.pojo.entity.Like;
import com.melon.videoservice.pojo.message.DeleteVideoMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "video-delete-topic", consumerGroup = "video-consumer-group")
@Slf4j
public class DeleteVideoConsumer implements RocketMQListener<DeleteVideoMessage> {
    @Resource
    private HdfsService hdfsService;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private CollectMapper collectMapper;

    @Override
    public void onMessage(DeleteVideoMessage deleteVideoMessage) {
        hdfsService.delete(deleteVideoMessage.getVideoPath(), true);
        hdfsService.delete(deleteVideoMessage.getCoverPath(), true);
        // delete video like when delete video
        if (likeMapper.delete(new LambdaQueryWrapper<Like>().eq(Like::getVideoId, deleteVideoMessage.getVideoId())) <= 0) {
            // delete failed
            log.error("Delete video like failed, videoId={}", deleteVideoMessage.getVideoId());
        }
        // delete video collect when delete video
        if (collectMapper.delete(new LambdaQueryWrapper<Collect>().eq(Collect::getVideoId, deleteVideoMessage.getVideoId())) <= 0) {
            // delete failed
            log.error("Delete video collect failed, videoId={}", deleteVideoMessage.getVideoId());
        }
    }
}
