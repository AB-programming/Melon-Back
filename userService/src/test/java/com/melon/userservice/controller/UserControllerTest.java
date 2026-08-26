package com.melon.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);

        GlobalController globalController = new GlobalController();
        ReflectionTestUtils.setField(globalController, "objectMapper", new ObjectMapper());
        ExceptionController exceptionController = new ExceptionController();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(globalController, exceptionController)
                .setValidator(validator)
                .build();
    }

    @Test
    void getUserById_returnsWrappedUser() throws Exception {
        when(userService.getUserById("u1")).thenReturn(UserVo.builder().id("u1").username("username").build());

        mockMvc.perform(get("/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("u1"));
    }

    @Test
    void createUser_returnsWrappedUser() throws Exception {
        when(userService.createUser("username", "pwd")).thenReturn(UserVo.builder().id("u1").username("username").build());

        mockMvc.perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\",\"password\":\"pwd\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("username"));
    }

    @Test
    void createUser_whenUsernameBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"pwd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").value("The username cannot empty!"));
    }

    @Test
    void updateUser_returnsWrappedUser() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(UserVo.builder().id("u1").nickname("newNick").build());

        mockMvc.perform(put("/u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"newNick\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("newNick"));
    }

    @Test
    void getUserListByIds_returnsWrappedMap() throws Exception {
        when(userService.getUserListByIds(any())).thenReturn(Map.of("u1", UserVo.builder().id("u1").build()));

        mockMvc.perform(get("/getUserListByIds").param("ids", "u1", "u2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.u1.id").value("u1"));
    }
}
