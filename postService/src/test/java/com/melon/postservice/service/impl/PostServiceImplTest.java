package com.melon.postservice.service.impl;

import com.melon.baseservice.service.HdfsService;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.postservice.mapper.LikeMapper;
import com.melon.postservice.mapper.PostMapper;
import com.melon.postservice.pojo.entity.Post;
import com.melon.postservice.pojo.vo.PostVo;
import com.melon.postservice.remote.UserRemote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private LikeMapper postLikeMapper;

    @Mock
    private UserRemote userRemote;

    @Mock
    private HdfsService hdfsService;

    @InjectMocks
    private PostServiceImpl postService;

    private UserVo userVo(String id) {
        return UserVo.builder().id(id).username("username").nickname("nickname").build();
    }

    private Post post(String id, String userId) {
        return Post.builder()
                .id(id)
                .userId(userId)
                .content("content")
                .images("a.png")
                .createdTime(LocalDateTime.now())
                .build();
    }

    private HttpResult<UserVo> okUser(String id) {
        return HttpResult.<UserVo>builder()
                .code(HttpResponseStatus.OK.getCode())
                .message(HttpResponseStatus.OK.getMessage())
                .data(userVo(id))
                .build();
    }

    @Test
    void addPost_success() throws ServerException {
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(postMapper.insert(any(Post.class))).thenReturn(1);
        MockMultipartFile file = new MockMultipartFile("images", "a.png", "image/png", new byte[]{1, 2, 3});

        PostVo result = postService.addPost("u1", "hello", new MockMultipartFile[]{file});

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getIsLike()).isFalse();
        assertThat(result.getLikeCount()).isZero();
        verify(hdfsService).upload(anyString(), any());
    }

    @Test
    void addPost_whenUserNotExist_throws() {
        when(userRemote.getUserById("u1")).thenReturn(
                HttpResult.<UserVo>builder().code(HttpResponseStatus.NOT_FOUND.getCode()).build());
        MockMultipartFile file = new MockMultipartFile("images", "a.png", "image/png", new byte[]{1});

        assertThatThrownBy(() -> postService.addPost("u1", "hello", new MockMultipartFile[]{file}))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user is not exist");
    }

    @Test
    void addPost_whenInsertFail_throws() throws Exception {
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(postMapper.insert(any(Post.class))).thenReturn(0);
        MockMultipartFile file = new MockMultipartFile("images", "a.png", "image/png", new byte[]{1});

        assertThatThrownBy(() -> postService.addPost("u1", "hello", new MockMultipartFile[]{file}))
                .isInstanceOf(ServerException.class)
                .hasMessage("Failed to add the post.");
    }

    @Test
    void deletePost_whenPostNotExist_doesNothing() throws ServerException {
        when(postMapper.selectById("p1")).thenReturn(null);

        postService.deletePost("p1");

        verify(postMapper, never()).deleteById(anyString());
    }

    @Test
    void deletePost_success() throws ServerException {
        when(postMapper.selectById("p1")).thenReturn(post("p1", "u1"));
        when(postMapper.deleteById("p1")).thenReturn(1);

        postService.deletePost("p1");

        verify(postLikeMapper).delete(any());
        verify(hdfsService).delete("/post/a.png", true);
    }

    @Test
    void getPostById_whenNotExist_throws() {
        when(postMapper.selectById("p1")).thenReturn(null);

        assertThatThrownBy(() -> postService.getPostById("p1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The post does not exist.");
    }

    @Test
    void getPostById_success() throws ServerException {
        when(postMapper.selectById("p1")).thenReturn(post("p1", "u1"));
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(postLikeMapper.selectCount(any())).thenReturn(2L);

        PostVo result = postService.getPostById("p1");

        assertThat(result.getId()).isEqualTo("p1");
        assertThat(result.getUser().getId()).isEqualTo("u1");
        assertThat(result.getIsLike()).isFalse();
        assertThat(result.getLikeCount()).isEqualTo(2L);
    }

    @Test
    void selectAllPost_success() {
        when(postMapper.selectList(any())).thenReturn(List.of(post("p1", "u1")));
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(postLikeMapper.selectCount(any())).thenReturn(0L);

        List<PostVo> result = postService.selectAllPost();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("p1");
    }

    @Test
    void selectFollowedPosts_whenNoSubscriptions_returnsEmpty() throws ServerException {
        when(userRemote.getMySubscriptions("u1")).thenReturn(
                HttpResult.<List<UserVo>>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(List.of())
                        .build());

        List<PostVo> result = postService.selectFollowedPosts("u1");

        assertThat(result).isEmpty();
    }

    @Test
    void selectFollowedPosts_success() throws ServerException {
        when(userRemote.getMySubscriptions("u1")).thenReturn(
                HttpResult.<List<UserVo>>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(List.of(userVo("u2")))
                        .build());
        when(postMapper.selectList(any())).thenReturn(List.of(post("p1", "u2")));
        when(postLikeMapper.exists(any())).thenReturn(false);
        when(postLikeMapper.selectCount(any())).thenReturn(0L);

        List<PostVo> result = postService.selectFollowedPosts("u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo("u2");
    }
}
