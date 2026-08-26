package com.melon.userservice;

import com.melon.commonservice.pojo.entity.User;
import com.melon.userservice.mapper.SubscriptionMapper;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.entity.Subscription;
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

    @Resource
    private SubscriptionMapper subscriptionMapper;

    @Test
    void userCrud() {
        String id = "test-" + UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("it_" + id)
                .password("pwd")
                .nickname("nickname")
                .build();
        assertThat(userMapper.insert(user)).isEqualTo(1);
        try {
            User found = userMapper.selectById(id);
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("it_" + id);

            found.setNickname("updated");
            assertThat(userMapper.updateById(found)).isEqualTo(1);
            assertThat(userMapper.selectById(id).getNickname()).isEqualTo("updated");
        } finally {
            userMapper.deleteById(id);
        }
    }

    @Test
    void subscriptionCrud() {
        String subscriber = "test-" + UUID.randomUUID();
        String target = "test-" + UUID.randomUUID();
        String id = "test-" + UUID.randomUUID();
        Subscription subscription = Subscription.builder()
                .id(id)
                .subscriber(subscriber)
                .target(target)
                .build();
        assertThat(subscriptionMapper.insert(subscription)).isEqualTo(1);
        try {
            assertThat(subscriptionMapper.selectById(id)).isNotNull();
            assertThat(subscriptionMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Subscription>()
                            .eq(Subscription::getSubscriber, subscriber))).isEqualTo(1L);
        } finally {
            subscriptionMapper.deleteById(id);
        }
    }
}
