package com.melon.baseservice.service.impl;

import com.melon.baseservice.service.HdfsService;
import jakarta.annotation.Resource;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class HdfsServiceImpl implements HdfsService {
    @Resource
    private FileSystem fs;

    public void upload(String destPath, InputStream input) {
        Path path = new Path(destPath);
        try (FSDataOutputStream out = fs.create(path)) {
            IOUtils.copyBytes(input, out, 4096, true);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FSDataInputStream open(String path) {
        try {
            return fs.open(new Path(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLength(String path) {
        try {
            return fs.getFileStatus(new Path(path)).getLen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String path, boolean recursive) {
        try {
            Path targetPath = new Path(path);
            return fs.delete(targetPath, recursive);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }
}
