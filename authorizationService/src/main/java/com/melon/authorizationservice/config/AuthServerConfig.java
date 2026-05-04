package com.melon.authorizationservice.config;

import com.melon.authorizationservice.oauth2.MelonPasswordAuthenticationConverter;
import com.melon.authorizationservice.oauth2.MelonPasswordAuthenticationProvider;
import com.melon.authorizationservice.pojo.AuthenticationUser;
import com.melon.authorizationservice.service.RedisOAuth2AuthorizationConsentService;
import com.melon.authorizationservice.service.RedisOAuth2AuthorizationService;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.commonservice.pojo.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthServerConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        authorizationServerConfigurer.authorizationEndpoint(endpoint -> endpoint.consentPage("/oauth2/consent"));

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/oauth2/token")
                        .ignoringRequestMatchers("/oauth2/revoke")
                        .ignoringRequestMatchers("/oauth2/introspect")
                )
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())
//                                .tokenEndpoint(tokenEndpoint -> {
//                                    tokenEndpoint.accessTokenRequestConverters(converters -> {
//                                                converters.add(new MelonPasswordAuthenticationConverter());
//                                            })
//                                            .authenticationProviders(providers -> {
//                                                providers.add(new MelonPasswordAuthenticationProvider());
//                                            });
//                                })
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
//                                .requestMatchers("/oauth2/token").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.cors(Customizer.withDefaults()).build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/bootstrap.min.js").permitAll()
                        .requestMatchers("/bootstrap.min.css").permitAll()
                        .requestMatchers("/bootstrap.min.css.map").permitAll()
                        .requestMatchers("/bootstrap.min.js.map").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/introspect").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login"));

        return http.cors(Customizer.withDefaults()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("melon")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantTypes(grantTypes -> {
//                    grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
//                    grantTypes.add(AuthorizationGrantType.PASSWORD);
//                })
                .redirectUri("http://localhost:3000/login")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofDays(3))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)
                        .build());
        RegisteredClient client = builder.build();
        return new InMemoryRegisteredClientRepository(client);
    }

    @Bean
    public RedisOAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository) {
        return new RedisOAuth2AuthorizationService(registeredClientRepository);
    }

    @Bean
    public RedisOAuth2AuthorizationConsentService authorizationConsentService() {
        return new RedisOAuth2AuthorizationConsentService();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("http://localhost:3000");
//        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Access-Control-Allow-Origin"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            Authentication authentication = context.getPrincipal();
            AuthenticationUser principal = (AuthenticationUser) authentication.getPrincipal();
            User user = principal.getUser();
            UserVo userVo = UserVo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatarUrl(user.getAvatarUrl())
                    .signature(user.getSignature())
                    .introduction(user.getIntroduction())
                    .residence(user.getResidence())
                    .gender(user.getGender())
                    .interest(user.getInterest())
                    .build();
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims((claims) -> {
                    claims.put("user", userVo);
                });
            }
        };
    }

//    @Bean
//    public JWKSource<SecurityContext> jwkSource() {
//        RSAKey rsaKey = Jwks.generateRsa();
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
//    }
//
//    @Bean
//    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
//        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
//    }
//
//    @Bean
//    public JwtEncoder jwtEncoder() {
//        KeyPair keyPair = generateRsaKey();
//        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
//                .privateKey((RSAPrivateKey) keyPair.getPrivate())
//                .keyID(UUID.randomUUID().toString())
//                .build();
//
//        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
//        return new NimbusJwtEncoder(jwkSource);
//    }
//
//    private KeyPair generateRsaKey() {
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            return keyPairGenerator.generateKeyPair();
//        } catch (Exception ex) {
//            throw new IllegalStateException(ex);
//        }
//    }
//
//    @Bean
//    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder) {
//        return new DelegatingOAuth2TokenGenerator(
//                new JwtGenerator(jwtEncoder),
//                new OAuth2AccessTokenGenerator(),
//                new OAuth2RefreshTokenGenerator()
//        );
//    }
}