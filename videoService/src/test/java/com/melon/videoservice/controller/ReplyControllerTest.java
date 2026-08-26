package com.melon.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.pojo.vo.ReplyVo;
import com.melon.videoservice.service.ReplyService;
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

class ReplyControllerTest {

    private MockMvc mockMvc;
    private ReplyService replyService;

    @BeforeEach
    void setUp() {
        replyService = mock(ReplyService.class);
        ReplyController controller = new ReplyController();
        ReflectionTestUtils.setField(controller, "replyService", replyService);

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
    void addReply_returnsWrappedReply() throws Exception {
        when(replyService.addReply("u1", "c", "c1", "c1", "content"))
                .thenReturn(ReplyVo.builder().id("r1").user(UserVo.builder().id("u1").build()).type("c").build());

        mockMvc.perform(post("/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"type\":\"c\",\"targetId\":\"c1\",\"commentId\":\"c1\",\"content\":\"content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("r1"));
    }

    @Test
    void addReply_whenTypeInvalid_returnsServerError() throws Exception {
        mockMvc.perform(post("/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"type\":\"x\",\"targetId\":\"c1\",\"commentId\":\"c1\",\"content\":\"content\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data").value("The type of reply is not correct"));
    }
}
