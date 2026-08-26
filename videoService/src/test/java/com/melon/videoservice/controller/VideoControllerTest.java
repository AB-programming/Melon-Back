package com.melon.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.config.ExceptionController;
import com.melon.commonservice.config.GlobalController;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.pojo.vo.VideoVo;
import com.melon.videoservice.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VideoControllerTest {

    private MockMvc mockMvc;
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        videoService = mock(VideoService.class);
        VideoController controller = new VideoController();
        ReflectionTestUtils.setField(controller, "videoService", videoService);

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
    void selectAllVideo_returnsWrappedList() throws Exception {
        when(videoService.selectAllVideo()).thenReturn(List.of(
                VideoVo.builder().id("v1").title("title").author(UserVo.builder().id("u1").build()).build()));

        mockMvc.perform(get("/selectAllVideo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("v1"));
    }

    @Test
    void getVideoInfo_returnsWrappedVideo() throws Exception {
        when(videoService.getVideoInfoById("v1")).thenReturn(
                VideoVo.builder().id("v1").title("title").author(UserVo.builder().id("u1").build()).build());

        mockMvc.perform(get("/getVideoInfo").param("videoId", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("title"));
    }

    @Test
    void checkMerge_returnsWrappedStatus() throws Exception {
        when(videoService.checkMergeResult("file1")).thenReturn("MERGING");

        mockMvc.perform(get("/checkMerge/file1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("MERGING"));
    }

    @Test
    void deleteVideo_returnsWrappedBoolean() throws Exception {
        when(videoService.deleteVideo("v1")).thenReturn(true);

        mockMvc.perform(delete("/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void check_returnsWrappedSet() throws Exception {
        when(videoService.check(anyString())).thenReturn(Set.of(0, 1));

        mockMvc.perform(post("/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileMd5\":\"md5\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void merge_returnsWrappedBoolean() throws Exception {
        when(videoService.merge("md5", "file1")).thenReturn(true);

        mockMvc.perform(post("/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileMd5\":\"md5\",\"id\":\"file1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
