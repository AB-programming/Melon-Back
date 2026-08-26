package com.melon.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.pojo.vo.CommentVo;
import com.melon.videoservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {

    private MockMvc mockMvc;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);
        CommentController controller = new CommentController();
        ReflectionTestUtils.setField(controller, "commentService", commentService);

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
    void addComment_returnsWrappedComment() throws Exception {
        when(commentService.addComment("u1", "v1", "nice"))
                .thenReturn(CommentVo.builder().id("c1").content("nice").user(UserVo.builder().id("u1").build()).build());

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"videoId\":\"v1\",\"content\":\"nice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("c1"));
    }

    @Test
    void addComment_whenContentBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"videoId\":\"v1\",\"content\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").value("The content cannot empty!"));
    }

    @Test
    void getCommentList_returnsWrappedList() throws Exception {
        when(commentService.getCommentListByUserIdAndVideoId("u1", "v1"))
                .thenReturn(List.of(CommentVo.builder().id("c1").content("nice").build()));

        mockMvc.perform(get("/comment/list").param("userId", "u1").param("videoId", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("c1"));
    }
}
