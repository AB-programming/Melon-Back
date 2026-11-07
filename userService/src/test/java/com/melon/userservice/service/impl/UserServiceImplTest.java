package com.melon.userservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.entity.User;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user(String id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .password("pwd")
                .nickname("nickname")
                .avatarUrl("http://example.com/avatar.png")
                .signature("signature")
                .build();
    }

    @Test
    void getUserById_success() {
        when(userMapper.selectById("u1")).thenReturn(user("u1", "username"));

        UserVo result = userService.getUserById("u1");

        assertThat(result.getId()).isEqualTo("u1");
        assertThat(result.getUsername()).isEqualTo("username");
        assertThat(result.getNickname()).isEqualTo("nickname");
    }

    @Test
    void updateUser_whenNotExist_throws() {
        when(userMapper.selectById("u1")).thenReturn(null);

        assertThatThrownBy(() -> userService.updateUser("u1", UserDto.builder().nickname("n").build()))
                .isInstanceOf(ServerException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void updateUser_success() throws ServerException {
        when(userMapper.selectById("u1")).thenReturn(user("u1", "username"));
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserVo result = userService.updateUser("u1", UserDto.builder().nickname("newNick").build());

        assertThat(result.getNickname()).isEqualTo("newNick");
    }

    @Test
    void updateUser_whenUpdateFail_throws() {
        when(userMapper.selectById("u1")).thenReturn(user("u1", "username"));
        when(userMapper.updateById(any(User.class))).thenReturn(0);

        assertThatThrownBy(() -> userService.updateUser("u1", UserDto.builder().nickname("newNick").build()))
                .isInstanceOf(ServerException.class)
                .hasMessage("Update failed, please try again later");
    }

    @Test
    void createUser_whenDuplicate_throws() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> userService.createUser("username", "pwd"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The user already exists");
    }

    @Test
    void createUser_success() throws ServerException {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        UserVo result = userService.createUser("username", "pwd");

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getUsername()).isEqualTo("username");
    }

    @Test
    void createUser_whenInsertFail_throws() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(0);

        assertThatThrownBy(() -> userService.createUser("username", "pwd"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Insert failed, please try again later");
    }

    @Test
    void getUserListByIds_whenEmpty_returnsEmptyMap() {
        Map<String, UserVo> result = userService.getUserListByIds(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void getUserListByIds_success() {
        when(userMapper.selectByIds(any())).thenReturn(List.of(user("u1", "username"), user("u2", "username2")));

        Map<String, UserVo> result = userService.getUserListByIds(List.of("u1", "u2"));

        assertThat(result).containsKeys("u1", "u2");
    }

    @Test
    void uploadAvatar_success() throws ServerException {
        ReflectionTestUtils.setField(userService, "uploadLocation", System.getProperty("java.io.tmpdir") + "/");
        ReflectionTestUtils.setField(userService, "staticUrl", "http://localhost:8080/user/static/");
        when(userMapper.selectById("u1")).thenReturn(user("u1", "username"));
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");

        String result = userService.uploadAvatar(file, "u1");

        assertThat(result).isEqualTo("http://localhost:8080/user/static/avatar/u1.png");
    }

    @Test
    void uploadAvatar_whenUpdateFail_throws() {
        ReflectionTestUtils.setField(userService, "uploadLocation", System.getProperty("java.io.tmpdir") + "/");
        ReflectionTestUtils.setField(userService, "staticUrl", "http://localhost:8080/user/static/");
        when(userMapper.selectById("u1")).thenReturn(user("u1", "username"));
        when(userMapper.updateById(any(User.class))).thenReturn(0);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");

        assertThatThrownBy(() -> userService.uploadAvatar(file, "u1"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Data update failed");
    }
}
