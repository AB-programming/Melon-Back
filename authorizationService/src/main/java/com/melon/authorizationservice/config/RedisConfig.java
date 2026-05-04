package com.melon.authorizationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${dev.redis.host}")
    private String host;

    @Value("${dev.redis.port}")
    private int port;

    @Value("${dev.redis.maxTotal}")
    private int maxTotal;

    @Value("${dev.redis.maxIdle}")
    private int maxIdle;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return this.redisTemplate(0);
    }

    public RedisTemplate<String, Object> redisTemplate(int database) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory(database));
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.afterPropertiesSet();
        return template;
    }

    RedisConnectionFactory redisConnectionFactory(int database) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
//        config.setPassword(password);
        config.setDatabase(database);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(
                jedisPoolConfig).build();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config, jedisClientConfiguration);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }
}
