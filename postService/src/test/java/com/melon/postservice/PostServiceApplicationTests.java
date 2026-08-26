package com.melon.postservice;

import com.melon.postservice.remote.UserRemote;
import org.apache.hadoop.fs.FileSystem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PostServiceApplicationTests {

    @MockBean
    private FileSystem fileSystem;

    @MockBean
    private UserRemote userRemote;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
    }

}
