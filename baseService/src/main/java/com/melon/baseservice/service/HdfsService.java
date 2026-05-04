package com.melon.baseservice.service;

import org.apache.hadoop.fs.FSDataInputStream;

import java.io.InputStream;

public interface HdfsService {
    void upload(String destPath, InputStream input);
    FSDataInputStream open(String path);
    long getLength(String path);
    boolean delete(String path, boolean recursive);
}

