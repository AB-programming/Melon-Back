package com.melon.baseservice.service;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

import java.io.IOException;
import java.io.InputStream;

public interface HdfsService {
    void upload(String destPath, InputStream input);
    FSDataInputStream open(String path);
    long getLength(String path);
    boolean delete(String path, boolean recursive);
    FSDataOutputStream createOutputStream(String path) throws IOException;
}

