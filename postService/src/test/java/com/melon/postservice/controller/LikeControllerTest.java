package com.melon.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LikeControllerTest {

    private MockMvc mockMvc;
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        likeService = mock(LikeService.class);
        LikeController controller = new LikeController();
        ReflectionTestUtils.setField(controller, "likeService", likeService);

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
    void addPostLike_returnsWrappedId() throws Exception {
        when(likeService.addLike("u1", "p1")).thenReturn("like_id");

        mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"postId\":\"p1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("like_id"));
    }

    @Test
    void addPostLike_whenUserIdBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"\",\"postId\":\"p1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").value("The userId cannot empty!"));
    }

    @Test
    void deletePostLike_invokesService() throws Exception {
        mockMvc.perform(delete("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"postId\":\"p1\"}"))
                .andExpect(status().isOk());

        verify(likeService).deleteLike("u1", "p1");
    }
}
