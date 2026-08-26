package com.melon.authorizationservice;

import com.melon.authorizationservice.mapper.UserMapper;
import com.melon.commonservice.pojo.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserMapperIntegrationTest {

    @Resource
    private UserMapper userMapper;

    @Test
    void userCrud() {
        String id = "test-" + UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("auth_it_" + id)
                .password("pwd")
                .nickname("nickname")
                .build();
        assertThat(userMapper.insert(user)).isEqualTo(1);
        try {
            User found = userMapper.selectById(id);
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("auth_it_" + id);
        } finally {
            userMapper.deleteById(id);
        }
    }
}
