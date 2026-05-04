package com.melon.gatewayservice;

import com.melon.gatewayservice.mapper.UserMapper;
import com.melon.gatewayservice.pojo.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class SimpleTests {
    @Resource
    private UserMapper userMapper;

    @Test
    public void test() {
        User user = User.builder().id("1").username("111").password("123").role("general").build();
        int insert = userMapper.insert(user);
        Assert.isTrue(insert == 1, "insert failed");
    }
}
