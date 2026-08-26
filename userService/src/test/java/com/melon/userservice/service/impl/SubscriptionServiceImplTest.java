package com.melon.userservice.service.impl;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.entity.User;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.mapper.SubscriptionMapper;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User user(String id) {
        return User.builder().id(id).username("u" + id).nickname("nickname").build();
    }

    @Test
    void addSubscription_whenTargetNotExist_throws() {
        when(userMapper.selectById("target")).thenReturn(null);

        assertThatThrownBy(() -> subscriptionService.addSubscription("u1", "target"))
                .isInstanceOf(ServerException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void addSubscription_whenAlreadySubscribed_throws() {
        when(userMapper.selectById("target")).thenReturn(user("target"));
        when(subscriptionMapper.exists(any())).thenReturn(true);

        assertThatThrownBy(() -> subscriptionService.addSubscription("u1", "target"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Subscription already exists");
    }

    @Test
    void addSubscription_success() throws ServerException {
        when(userMapper.selectById("target")).thenReturn(user("target"));
        when(subscriptionMapper.exists(any())).thenReturn(false);
        when(subscriptionMapper.insert(any(Subscription.class))).thenReturn(1);

        String id = subscriptionService.addSubscription("u1", "target");

        assertThat(id).isNotBlank();
    }

    @Test
    void isSubscribed_returnsTrue() {
        when(subscriptionMapper.exists(any())).thenReturn(true);

        assertThat(subscriptionService.isSubscribed("u1", "target")).isTrue();
    }

    @Test
    void deleteSubscription_whenTargetNotExist_throws() {
        when(userMapper.selectById("target")).thenReturn(null);

        assertThatThrownBy(() -> subscriptionService.deleteSubscription("u1", "target"))
                .isInstanceOf(ServerException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void deleteSubscription_whenNotSubscribed_doesNothing() throws ServerException {
        when(userMapper.selectById("target")).thenReturn(user("target"));
        when(subscriptionMapper.exists(any())).thenReturn(false);

        subscriptionService.deleteSubscription("u1", "target");

        verify(subscriptionMapper, never()).delete(any());
    }

    @Test
    void deleteSubscription_success() throws ServerException {
        when(userMapper.selectById("target")).thenReturn(user("target"));
        when(subscriptionMapper.exists(any())).thenReturn(true);

        subscriptionService.deleteSubscription("u1", "target");

        verify(subscriptionMapper).delete(any());
    }

    @Test
    void getSubscriptionCount_success() {
        when(subscriptionMapper.selectCount(any())).thenReturn(5L);

        assertThat(subscriptionService.getSubscriptionCount("u1")).isEqualTo(5L);
    }

    @Test
    void getMySubscriptions_success() {
        when(subscriptionMapper.selectList(any())).thenReturn(List.of(
                Subscription.builder().id("s1").subscriber("u1").target("u2").build()));
        when(userMapper.selectById("u2")).thenReturn(user("u2"));

        List<UserVo> result = subscriptionService.getMySubscriptions("u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("u2");
    }
}
