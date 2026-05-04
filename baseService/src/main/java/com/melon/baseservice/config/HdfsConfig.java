package com.melon.baseservice.config;

import jakarta.servlet.MultipartConfigElement;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ComponentScan(basePackages = "com.melon.baseservice")
public class HdfsConfig {
    @Value("${file.maxFileSize}")
    private long maxFileSize;

    @Value("${file.maxRequestSize}")
    private long maxRequestSize;

    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.user}")
    private String hdfsUser;

    @Bean(destroyMethod = "close")
    public FileSystem fileSystem() throws URISyntaxException, IOException, InterruptedException {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("dfs.replication", "1");
        return org.apache.hadoop.fs.FileSystem.get(new URI(hdfsUri), conf, hdfsUser);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory multipartConfigFactory = new MultipartConfigFactory();
        multipartConfigFactory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        multipartConfigFactory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));
        return multipartConfigFactory.createMultipartConfig();
    }
}

