package com.melon.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SubscriptionControllerTest {

    private MockMvc mockMvc;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = mock(SubscriptionService.class);
        SubscriptionController controller = new SubscriptionController();
        ReflectionTestUtils.setField(controller, "subscriptionService", subscriptionService);

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
    void addSubscription_returnsWrappedId() throws Exception {
        when(subscriptionService.addSubscription("u1", "u2")).thenReturn("sub_id");

        mockMvc.perform(post("/subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subscriber\":\"u1\",\"target\":\"u2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("sub_id"));
    }

    @Test
    void getFans_returnsWrappedCount() throws Exception {
        when(subscriptionService.getSubscriptionCount("u1")).thenReturn(42L);

        mockMvc.perform(get("/subscription/getFans").param("userId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(42));
    }

    @Test
    void getMySubscriptions_returnsWrappedList() throws Exception {
        when(subscriptionService.getMySubscriptions("u1"))
                .thenReturn(List.of(UserVo.builder().id("u2").username("username2").build()));

        mockMvc.perform(get("/subscription/getMySubscriptions").param("subscriber", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("u2"));
    }

    @Test
    void cancelSubscription_invokesService() throws Exception {
        mockMvc.perform(delete("/subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subscriber\":\"u1\",\"target\":\"u2\"}"))
                .andExpect(status().isOk());

        verify(subscriptionService).deleteSubscription("u1", "u2");
    }
}
