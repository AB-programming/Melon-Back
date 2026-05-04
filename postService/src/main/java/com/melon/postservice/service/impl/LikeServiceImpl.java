package com.melon.postservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.postservice.mapper.LikeMapper;
import com.melon.postservice.mapper.PostMapper;
import com.melon.postservice.pojo.entity.Post;
import com.melon.postservice.pojo.entity.PostLike;
import com.melon.postservice.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private LikeMapper likeMapper;

    @Resource
    private PostMapper postMapper;

    @Override
    public String addLike(String userId, String postId) throws ServerException {
        Post post = postMapper.selectById(postId);
        if (Objects.isNull(post)) {
            throw new ServerException("The post is not exists");
        }
        if (this.exists(userId, postId)) {
            throw new ServerException("The user already like this post!");
        }
        String id = UUID.randomUUID().toString();
        PostLike postLike = PostLike.builder()
                .id(id)
                .userId(userId)
                .postId(postId)
                .build();
        likeMapper.insert(postLike);
        return id;
    }

    @Override
    public Boolean exists(String userId, String postId) {
        LambdaQueryWrapper<PostLike> postLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLikeLambdaQueryWrapper.allEq(Map.of(PostLike::getUserId, userId,
                PostLike::getPostId, postId));
        return likeMapper.exists(postLikeLambdaQueryWrapper);
    }

    @Override
    public void deleteLike(String userId, String postId) {
        LambdaQueryWrapper<PostLike> postLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLikeLambdaQueryWrapper.allEq(Map.of(PostLike::getUserId, userId,
                PostLike::getPostId, postId));
        likeMapper.delete(postLikeLambdaQueryWrapper);
    }
}
