package com.melon.videoservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.mapper.CommentLikeMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.CommentLike;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {

    @Mock
    private CommentLikeMapper commentLikemapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentLikeServiceImpl commentLikeService;

    private Comment comment(String id) {
        return Comment.builder().id(id).userId("u1").videoId("v1").content("c").createdTime(LocalDateTime.now()).build();
    }

    @Test
    void addCommentLike_success() throws ServerException {
        when(commentMapper.selectById("c1")).thenReturn(comment("c1"));
        when(commentLikemapper.exists(any())).thenReturn(false);
        when(commentLikemapper.insert(any(CommentLike.class))).thenReturn(1);

        String id = commentLikeService.addCommentLike("u1", "c1");

        assertThat(id).isNotBlank();
    }

    @Test
    void addCommentLike_whenCommentNotExist_throws() {
        when(commentMapper.selectById("c1")).thenReturn(null);

        assertThatThrownBy(() -> commentLikeService.addCommentLike("u1", "c1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The comment is not exists!");
    }

    @Test
    void addCommentLike_whenAlreadyLiked_throws() {
        when(commentMapper.selectById("c1")).thenReturn(comment("c1"));
        when(commentLikemapper.exists(any())).thenReturn(true);

        assertThatThrownBy(() -> commentLikeService.addCommentLike("u1", "c1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user already like this comment!");
    }

    @Test
    void deleteCommentLike_success() throws ServerException {
        when(commentLikemapper.delete(any())).thenReturn(1);

        commentLikeService.deleteCommentLike("u1", "c1");
    }

    @Test
    void deleteCommentLike_whenDeleteFail_throws() {
        when(commentLikemapper.delete(any())).thenReturn(0);

        assertThatThrownBy(() -> commentLikeService.deleteCommentLike("u1", "c1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Failed to delete the comment, please try again later");
    }

    @Test
    void exists_returnsTrue() {
        when(commentLikemapper.exists(any())).thenReturn(true);

        assertThat(commentLikeService.exists("u1", "c1")).isTrue();
    }
}
