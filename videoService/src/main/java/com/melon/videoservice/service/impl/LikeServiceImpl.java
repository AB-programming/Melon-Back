package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.LikeMapper;
import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Like;
import com.melon.videoservice.pojo.entity.Video;
import com.melon.videoservice.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private VideoMapper videoMapper;

    @Resource
    private LikeMapper likeMapper;

    @Override
    public String addLike(String userId, String videoId) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video is not exists!");
        }
        if (this.exists(userId, videoId)) {
            throw new ServerException("The user already like this video!");
        }
        String id = UUID.randomUUID().toString();
        Like like = Like.builder()
                .id(id)
                .userId(userId)
                .videoId(videoId)
                .build();
        if (likeMapper.insert(like) <= 0) {
            throw new ServerException("Like failed, please try again!");
        }
        return id;
    }

    @Override
    public Boolean exists(String userId, String videoId) throws ServerException {
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.allEq(Map.of(Like::getUserId, userId, Like::getVideoId, videoId));
        return likeMapper.exists(likeLambdaQueryWrapper);
    }

    @Override
    public void deleteLike(String userId, String videoId) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video is not exists!");
        }
        if (this.exists(userId, videoId)) {
            LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likeLambdaQueryWrapper.allEq(Map.of(Like::getUserId, userId, Like::getVideoId, videoId));
            likeMapper.delete(likeLambdaQueryWrapper);
        }
    }

    @Override
    public long getLikeCount(String videoId) throws ServerException {
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(Like::getVideoId, videoId);
        return likeMapper.selectCount(likeLambdaQueryWrapper);
    }
}
