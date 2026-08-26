package com.melon.videoservice;

import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Video;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VideoMapperIntegrationTest {


    @Resource
    private VideoMapper videoMapper;

    @Test
    void videoCrud() {
        String id = "test-" + UUID.randomUUID();
        Video video = Video.builder()
                .id(id)
                .userId("test-user")
                .videoPath("/video/" + id + ".mp4")
                .picturePath("/cover/" + id + ".png")
                .title("integration title")
                .description("integration description")
                .build();
        assertThat(videoMapper.insert(video)).isEqualTo(1);
        try {
            Video found = videoMapper.selectById(id);
            assertThat(found).isNotNull();
            assertThat(found.getTitle()).isEqualTo("integration title");
        } finally {
            videoMapper.deleteById(id);
        }
    }
}
