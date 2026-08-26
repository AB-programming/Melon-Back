package com.melon.postservice;

import com.melon.postservice.mapper.PostMapper;
import com.melon.postservice.pojo.entity.Post;
import com.melon.postservice.remote.UserRemote;
import jakarta.annotation.Resource;
import org.apache.hadoop.fs.FileSystem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PostMapperIntegrationTest {

    @MockBean
    private FileSystem fileSystem;

    @MockBean
    private UserRemote userRemote;

    @Resource
    private PostMapper postMapper;

    @Test
    void postCrud() {
        String id = "test-" + UUID.randomUUID();
        Post post = Post.builder()
                .id(id)
                .userId("test-user")
                .content("hello integration test")
                .images("a.png")
                .createdTime(LocalDateTime.now())
                .build();
        assertThat(postMapper.insert(post)).isEqualTo(1);
        try {
            Post found = postMapper.selectById(id);
            assertThat(found).isNotNull();
            assertThat(found.getContent()).isEqualTo("hello integration test");
        } finally {
            postMapper.deleteById(id);
        }
    }
}
