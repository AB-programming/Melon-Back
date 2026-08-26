package com.melon.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.videoservice.service.CommentLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentLikeControllerTest {

    private MockMvc mockMvc;
    private CommentLikeService commentLikeService;

    @BeforeEach
    void setUp() {
        commentLikeService = mock(CommentLikeService.class);
        CommentLikeController controller = new CommentLikeController();
        ReflectionTestUtils.setField(controller, "commentLikeService", commentLikeService);

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
    void addCommentLike_returnsWrappedId() throws Exception {
        when(commentLikeService.addCommentLike("u1", "c1")).thenReturn("cl_id");

        mockMvc.perform(post("/commentLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"commentId\":\"c1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("cl_id"));
    }
}
