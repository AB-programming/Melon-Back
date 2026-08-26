package com.melon.videoservice.service.impl;

import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.Reply;
import com.melon.videoservice.pojo.vo.ReplyVo;
import com.melon.videoservice.remote.UserRemote;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyServiceImplTest {

    @Mock
    private ReplyMapper replyMapper;

    @Mock
    private UserRemote userRemote;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ReplyServiceImpl replyService;

    private HttpResult<UserVo> okUser(String id) {
        return HttpResult.<UserVo>builder()
                .code(HttpResponseStatus.OK.getCode())
                .data(UserVo.builder().id(id).username("username").build())
                .build();
    }

    @Test
    void addReply_whenReplyComment_success() throws ServerException {
        when(commentMapper.selectById("c1")).thenReturn(Comment.builder().id("c1").userId("cu").build());
        when(replyMapper.insert(any(Reply.class))).thenReturn(1);
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(userRemote.getUserById("cu")).thenReturn(okUser("cu"));

        ReplyVo result = replyService.addReply("u1", "c", "c1", "c1", "content");

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getUser().getId()).isEqualTo("u1");
        assertThat(result.getTargetUser().getId()).isEqualTo("cu");
        assertThat(result.getType()).isEqualTo("c");
    }

    @Test
    void addReply_whenReplyReply_success() throws ServerException {
        when(replyMapper.selectById("r1")).thenReturn(Reply.builder().id("r1").userId("ru").build());
        when(replyMapper.insert(any(Reply.class))).thenReturn(1);
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));
        when(userRemote.getUserById("ru")).thenReturn(okUser("ru"));

        ReplyVo result = replyService.addReply("u1", "r", "r1", "c1", "content");

        assertThat(result.getTargetUser().getId()).isEqualTo("ru");
        assertThat(result.getType()).isEqualTo("r");
    }

    @Test
    void addReply_whenInsertFail_throws() {
        when(commentMapper.selectById("c1")).thenReturn(Comment.builder().id("c1").userId("cu").build());
        when(replyMapper.insert(any(Reply.class))).thenReturn(0);

        assertThatThrownBy(() -> replyService.addReply("u1", "c", "c1", "c1", "content"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Reply failed, please try again");
    }

    @Test
    void exists_returnsTrue() {
        when(replyMapper.exists(any())).thenReturn(true);

        assertThat(replyService.exists("u1", "c", "c1")).isTrue();
    }

    @Test
    void deleteReplyByReplyId_success() throws ServerException {
        when(replyMapper.deleteById("r1")).thenReturn(1);

        replyService.deleteReplyByReplyId("r1");
    }

    @Test
    void deleteReplyByReplyId_whenFail_throws() {
        when(replyMapper.deleteById("r1")).thenReturn(0);

        assertThatThrownBy(() -> replyService.deleteReplyByReplyId("r1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Failed to delete the reply, please try again later");
    }
}
