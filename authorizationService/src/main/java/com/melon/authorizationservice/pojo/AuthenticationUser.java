package com.melon.authorizationservice.pojo;

import com.melon.commonservice.pojo.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.List;

public class AuthenticationUser implements UserDetails {
    @Getter
    private final User user;

    private List<SimpleGrantedAuthority> authorities;

    public AuthenticationUser(User user) {
        this.user = user;
    }

    public void setAuthorityList(List<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return new BCryptPasswordEncoder().encode(user.getPassword());
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
