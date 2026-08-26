package com.melon.postservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.postservice.mapper.LikeMapper;
import com.melon.postservice.mapper.PostMapper;
import com.melon.postservice.pojo.entity.Post;
import com.melon.postservice.pojo.entity.PostLike;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Post post(String id) {
        return Post.builder().id(id).userId("u1").content("c").createdTime(LocalDateTime.now()).build();
    }

    @Test
    void addLike_success() throws ServerException {
        when(postMapper.selectById("p1")).thenReturn(post("p1"));
        when(likeMapper.exists(any())).thenReturn(false);
        when(likeMapper.insert(any(PostLike.class))).thenReturn(1);

        String id = likeService.addLike("u1", "p1");

        assertThat(id).isNotBlank();
        verify(likeMapper).insert(any(PostLike.class));
    }

    @Test
    void addLike_whenPostNotExist_throws() {
        when(postMapper.selectById("p1")).thenReturn(null);

        assertThatThrownBy(() -> likeService.addLike("u1", "p1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The post is not exists");
    }

    @Test
    void addLike_whenAlreadyLiked_throws() {
        when(postMapper.selectById("p1")).thenReturn(post("p1"));
        when(likeMapper.exists(any())).thenReturn(true);

        assertThatThrownBy(() -> likeService.addLike("u1", "p1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user already like this post!");
    }

    @Test
    void exists_returnsTrue() {
        when(likeMapper.exists(any())).thenReturn(true);

        assertThat(likeService.exists("u1", "p1")).isTrue();
    }

    @Test
    void deleteLike_invokesMapper() {
        likeService.deleteLike("u1", "p1");

        verify(likeMapper).delete(any());
    }
}
