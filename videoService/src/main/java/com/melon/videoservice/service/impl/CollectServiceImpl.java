package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.CollectMapper;
import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Collect;
import com.melon.videoservice.pojo.entity.Video;
import com.melon.videoservice.service.CollectService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CollectServiceImpl implements CollectService {
    @Resource
    private CollectMapper collectMapper;

    @Resource
    private VideoMapper videoMapper;

    @Override
    public String addCollect(String userId, String videoId) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video is not exists!");
        }
        if (this.exists(userId, videoId)) {
            throw new ServerException("The user already like this video!");
        }
        String id = UUID.randomUUID().toString();
        Collect collect = Collect.builder()
                .id(id)
                .userId(userId)
                .videoId(videoId)
                .build();
        if (collectMapper.insert(collect) <= 0) {
            throw new ServerException("Collect failed, please try again!");
        }
        return id;
    }

    @Override
    public Boolean exists(String userId, String videoId) {
        LambdaQueryWrapper<Collect> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.allEq(Map.of(Collect::getUserId, userId, Collect::getVideoId, videoId));
        return collectMapper.exists(likeLambdaQueryWrapper);
    }

    @Override
    public void deleteCollect(String userId, String videoId) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video is not exists!");
        }
        if (this.exists(userId, videoId)) {
            LambdaQueryWrapper<Collect> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likeLambdaQueryWrapper.allEq(Map.of(Collect::getUserId, userId, Collect::getVideoId, videoId));
            collectMapper.delete(likeLambdaQueryWrapper);
        }
    }
}
