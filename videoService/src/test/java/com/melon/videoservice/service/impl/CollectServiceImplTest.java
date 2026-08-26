package com.melon.videoservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.CollectMapper;
import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Collect;
import com.melon.videoservice.pojo.entity.Video;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectServiceImplTest {

    @Mock
    private CollectMapper collectMapper;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private CollectServiceImpl collectService;

    private Video video(String id) {
        return Video.builder().id(id).userId("u1").build();
    }

    @Test
    void addCollect_success() throws ServerException {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(collectMapper.exists(any())).thenReturn(false);
        when(collectMapper.insert(any(Collect.class))).thenReturn(1);

        String id = collectService.addCollect("u1", "v1");

        assertThat(id).isNotBlank();
    }

    @Test
    void addCollect_whenVideoNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> collectService.addCollect("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video is not exists!");
    }

    @Test
    void addCollect_whenAlreadyCollected_throws() {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(collectMapper.exists(any())).thenReturn(true);

        assertThatThrownBy(() -> collectService.addCollect("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user already like this video!");
    }

    @Test
    void deleteCollect_whenNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> collectService.deleteCollect("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video is not exists!");
    }

    @Test
    void deleteCollect_success() throws ServerException {
        when(videoMapper.selectById("v1")).thenReturn(video("v1"));
        when(collectMapper.exists(any())).thenReturn(true);

        collectService.deleteCollect("u1", "v1");

        verify(collectMapper).delete(any());
    }
}
