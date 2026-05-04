package com.melon.authorizationservice.service;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final Map<String, OAuth2AuthorizationConsent> authorizationConsentList;

    public RedisOAuth2AuthorizationConsentService() {
        this.authorizationConsentList = new HashMap<>();
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent must not be null");
        this.authorizationConsentList.put(authorizationConsent.getRegisteredClientId(), authorizationConsent);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent must not be null");
        this.authorizationConsentList.remove(authorizationConsent.getRegisteredClientId());
    }

    @Nullable
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return null;
    }

}