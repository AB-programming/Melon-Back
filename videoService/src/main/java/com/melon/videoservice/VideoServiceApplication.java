package com.melon.videoservice;

import com.melon.baseservice.config.HdfsConfig;
import com.melon.baseservice.config.RedisConfig;
import com.melon.commonservice.config.SecurityConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.melon.videoservice.mapper")
@EnableFeignClients
@ComponentScan(basePackages = "com.melon.videoservice", basePackageClasses = {SecurityConfig.class, HdfsConfig.class, RedisConfig.class})
public class VideoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoServiceApplication.class, args);
	}

}
