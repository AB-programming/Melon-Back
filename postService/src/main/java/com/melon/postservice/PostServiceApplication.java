package com.melon.postservice;

import com.melon.baseservice.config.HdfsConfig;
import com.melon.commonservice.config.SecurityConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.melon.postservice.mapper")
@EnableFeignClients
@ComponentScan(basePackages = "com.melon.postservice", basePackageClasses = {SecurityConfig.class, HdfsConfig.class})
public class PostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

}
