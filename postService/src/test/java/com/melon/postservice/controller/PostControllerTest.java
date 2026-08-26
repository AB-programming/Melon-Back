package com.melon.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.postservice.pojo.vo.PostVo;
import com.melon.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest {

    private MockMvc mockMvc;
    private PostService postService;

    private PostVo postVo(String id, String userId) {
        return PostVo.builder()
                .id(id)
                .content("content")
                .user(UserVo.builder().id(userId).username("username").build())
                .images(List.of("a.png"))
                .createdTime("2024-01-01 00:00:00")
                .isLike(false)
                .likeCount(0L)
                .build();
    }

    @BeforeEach
    void setUp() {
        postService = mock(PostService.class);
        PostController controller = new PostController();
        ReflectionTestUtils.setField(controller, "postService", postService);

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
    void selectAllPost_returnsWrappedList() throws Exception {
        when(postService.selectAllPost()).thenReturn(List.of(postVo("p1", "u1")));

        mockMvc.perform(get("/selectAllPost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("p1"));
    }

    @Test
    void getPostById_returnsWrappedPost() throws Exception {
        when(postService.getPostById("p1")).thenReturn(postVo("p1", "u1"));

        mockMvc.perform(get("/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("p1"));
    }

    @Test
    void getPostById_whenNotFound_returnsServerError() throws Exception {
        when(postService.getPostById("p404")).thenThrow(new ServerException("The post does not exist."));

        mockMvc.perform(get("/p404"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data").value("The post does not exist."));
    }

    @Test
    void selectPostListWithUserId_returnsWrappedList() throws Exception {
        when(postService.selectPostListWithUserId("u1")).thenReturn(List.of(postVo("p1", "u1")));

        mockMvc.perform(get("/selectPostListWithUserId").param("userId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("p1"));
    }

    @Test
    void deletePost_returnsOk() throws Exception {
        mockMvc.perform(delete("/p1"))
                .andExpect(status().isOk());
    }
}
