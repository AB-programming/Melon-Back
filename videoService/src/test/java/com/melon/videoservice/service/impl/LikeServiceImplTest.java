package com.melon.videoservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.LikeMapper;
import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Like;
import com.melon.videoservice.pojo.entity.Video;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Video video(String id) {
        return Video.builder().id(id).userId("u1").build();
    }

    @Test
    void addLike_success() throws ServerException {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(likeMapper.exists(any())).thenReturn(false);
        when(likeMapper.insert(any(Like.class))).thenReturn(1);

        String id = likeService.addLike("u1", "v1");

        assertThat(id).isNotBlank();
    }

    @Test
    void addLike_whenVideoNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> likeService.addLike("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video is not exists!");
    }

    @Test
    void addLike_whenAlreadyLiked_throws() {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(likeMapper.exists(any())).thenReturn(true);

        assertThatThrownBy(() -> likeService.addLike("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user already like this video!");
    }

    @Test
    void addLike_whenInsertFail_throws() {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(likeMapper.exists(any())).thenReturn(false);
        when(likeMapper.insert(any(Like.class))).thenReturn(0);

        assertThatThrownBy(() -> likeService.addLike("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Like failed, please try again!");
    }

    @Test
    void deleteLike_whenNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> likeService.deleteLike("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video is not exists!");
    }

    @Test
    void deleteLike_success() throws ServerException {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(likeMapper.exists(any())).thenReturn(true);

        likeService.deleteLike("u1", "v1");

        verify(likeMapper).delete(any());
    }

    @Test
    void getLikeCount_success() throws ServerException {
        when(likeMapper.selectCount(any())).thenReturn(5L);

        assertThat(likeService.getLikeCount("v1")).isEqualTo(5L);
    }
}
