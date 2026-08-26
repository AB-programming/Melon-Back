package com.melon.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.videoservice.service.CollectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CollectControllerTest {

    private MockMvc mockMvc;
    private CollectService collectService;

    @BeforeEach
    void setUp() {
        collectService = mock(CollectService.class);
        CollectController controller = new CollectController();
        ReflectionTestUtils.setField(controller, "collectService", collectService);

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
    void addCollect_returnsWrappedId() throws Exception {
        when(collectService.addCollect("u1", "v1")).thenReturn("collect_id");

        mockMvc.perform(post("/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\",\"videoId\":\"v1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("collect_id"));
    }

    @Test
    void isCollect_returnsWrappedBoolean() throws Exception {
        when(collectService.exists("u1", "v1")).thenReturn(true);

        mockMvc.perform(get("/collect/isCollect").param("userId", "u1").param("videoId", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
