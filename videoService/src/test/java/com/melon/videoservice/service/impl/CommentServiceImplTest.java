package com.melon.videoservice.service.impl;

import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.CommentLikeMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.vo.CommentVo;
import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.DeleteCommentProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentLikeMapper commentLikeMapper;

    @Mock
    private UserRemote userRemote;

    @Mock
    private ReplyMapper replyMapper;

    @Mock
    private DeleteCommentProducer deleteCommentProducer;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addComment_success() throws ServerException {
        when(commentMapper.insert(any(Comment.class))).thenReturn(1);
        when(userRemote.getUserById("u1")).thenReturn(
                HttpResult.<UserVo>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(UserVo.builder().id("u1").username("username").build())
                        .build());

        CommentVo result = commentService.addComment("u1", "v1", "nice video");

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getContent()).isEqualTo("nice video");
        assertThat(result.getUser().getId()).isEqualTo("u1");
        assertThat(result.getIsLiked()).isFalse();
        assertThat(result.getLikeCount()).isZero();
    }

    @Test
    void addComment_whenInsertFail_throws() {
        when(commentMapper.insert(any(Comment.class))).thenReturn(0);

        assertThatThrownBy(() -> commentService.addComment("u1", "v1", "nice video"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Add comment failed, please try again");
    }

    @Test
    void getCommentList_success() throws ServerException {
        Comment comment = Comment.builder().id("c1").userId("u1").videoId("v1").content("c")
                .createdTime(LocalDateTime.now()).build();
        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(replyMapper.selectList(any())).thenReturn(List.of());
        when(userRemote.getUserListByIds(any())).thenReturn(
                HttpResult.<Map<String, UserVo>>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(Map.of("u1", UserVo.builder().id("u1").username("username").build()))
                        .build());
        when(commentLikeMapper.selectCount(any())).thenReturn(0L);
        when(commentLikeMapper.exists(any())).thenReturn(false);

        List<CommentVo> result = commentService.getCommentListByUserIdAndVideoId("u1", "v1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("c1");
        assertThat(result.get(0).getUser().getId()).isEqualTo("u1");
    }

    @Test
    void getCommentList_whenUserModuleException_throws() {
        when(commentMapper.selectList(any())).thenReturn(List.of(
                Comment.builder().id("c1").userId("u1").videoId("v1").createdTime(LocalDateTime.now()).build()));
        when(replyMapper.selectList(any())).thenReturn(List.of());
        when(userRemote.getUserListByIds(any())).thenReturn(
                HttpResult.<Map<String, UserVo>>builder().code(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode()).build());

        assertThatThrownBy(() -> commentService.getCommentListByUserIdAndVideoId("u1", "v1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("user module exception");
    }

    @Test
    void deleteCommentByCommentId_success() throws ServerException {
        when(commentMapper.deleteById("c1")).thenReturn(1);

        commentService.deleteCommentByCommentId("c1");

        verify(deleteCommentProducer).sendDeleteCommentMessage("c1");
    }

    @Test
    void deleteCommentByCommentId_whenFail_throws() {
        when(commentMapper.deleteById("c1")).thenReturn(0);

        assertThatThrownBy(() -> commentService.deleteCommentByCommentId("c1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Failed to delete the comment, please try again later");
    }
}
