package com.melon.authorizationservice.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.melon.authorizationservice.mapper.UserMapper;
import com.melon.authorizationservice.pojo.AuthenticationUser;
import com.melon.commonservice.pojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUserDetailsService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!userMapper.exists(new QueryWrapper<User>().lambda().eq(User::getUsername, username))) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));
        return new AuthenticationUser(user);
    }
}
