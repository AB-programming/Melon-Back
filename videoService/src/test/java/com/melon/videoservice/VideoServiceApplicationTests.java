package com.melon.videoservice;

import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.DeleteCommentConsumer;
import com.melon.videoservice.service.DeleteCommentProducer;
import com.melon.videoservice.service.DeleteVideoConsumer;
import com.melon.videoservice.service.DeleteVideoProducer;
import com.melon.videoservice.service.MergeConsumer;
import com.melon.videoservice.service.MergeProducer;
import org.apache.hadoop.fs.FileSystem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class VideoServiceApplicationTests {

    @MockBean
    private FileSystem fileSystem;

    @MockBean
    private UserRemote userRemote;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private MergeProducer mergeProducer;

    @MockBean
    private DeleteVideoProducer deleteVideoProducer;

    @MockBean
    private DeleteCommentProducer deleteCommentProducer;

    @MockBean
    private MergeConsumer mergeConsumer;

    @MockBean
    private DeleteVideoConsumer deleteVideoConsumer;

    @MockBean
    private DeleteCommentConsumer deleteCommentConsumer;

    @Test
    void contextLoads() {
    }

}
