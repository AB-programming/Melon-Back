package com.melon.authorizationservice.service;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final RegisteredClientRepository registeredClientRepository;
    private final Map<String, OAuth2Authorization> authorizationMap;

    public RedisOAuth2AuthorizationService(RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationMap = new HashMap<>();
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.authorizationMap.put(authorization.getId(), authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.authorizationMap.remove(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.authorizationMap.get(id);
    }

    @Nullable
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        Assert.notNull(tokenType, "tokenType cannot be null");
        Iterator<OAuth2Authorization> iterator = this.authorizationMap.values().iterator();
        if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            while (iterator.hasNext()) {
                OAuth2Authorization authorization = iterator.next();
                if (authorization.getAttribute(OAuth2ParameterNames.STATE) != null && authorization.getAttribute(OAuth2ParameterNames.STATE).equals(token)) {
                    return authorization;
                }
            }
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            while (iterator.hasNext()) {
                OAuth2Authorization authorization = iterator.next();
                if (Objects.nonNull(authorization.getToken(token))) {
                    return authorization;
                }
            }
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            while (iterator.hasNext()) {
                OAuth2Authorization authorization = iterator.next();
                if (Objects.nonNull(authorization.getToken(token))) {
                    return authorization;
                }
            }
        }
        return null;
    }
}