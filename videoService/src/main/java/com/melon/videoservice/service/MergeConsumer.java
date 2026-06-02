package com.melon.videoservice.service;

import com.melon.baseservice.service.HdfsService;
import com.melon.videoservice.pojo.message.MergeMessage;
import jakarta.annotation.Resource;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@Component
@RocketMQMessageListener(topic = "video-merge-topic", consumerGroup = "merge-consumer-group")
public class MergeConsumer implements RocketMQListener<MergeMessage> {
    @Resource
    private HdfsService hdfsService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(MergeMessage mergeMessage) {
        String key = "merge:status:" + mergeMessage.getFileId();
        redisTemplate.opsForValue().set(key, "MERGING");
        String dirPath = System.getProperty("user.dir")
                + File.separator
                + "upload-temp"
                + File.separator
                + mergeMessage.getFileMd5();
        File chunkDir = new File(dirPath);
        File[] chunkFiles = chunkDir.listFiles();
        Arrays.sort(Objects.requireNonNull(chunkFiles),
                Comparator.comparingInt(file ->
                        Integer.parseInt(file.getName().replace(".part", ""))
                ));

        String mergePath = "/video/" + mergeMessage.getFileId() + ".mp4";

        boolean flag = false;
        byte[] buffer = new byte[256 * 1024];
        try (FSDataOutputStream bos = hdfsService.createOutputStream(mergePath)) {
            for (File chunkFile : chunkFiles) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunkFile));
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
            }
            bos.hflush();
            bos.hsync();
            redisTemplate.opsForValue().set(key, "SUCCESS");
            // setup expire
            redisTemplate.expire(key, Duration.ofMinutes(10));
            // delete upload chunk cache
            redisTemplate.delete("upload:chunks:" + mergeMessage.getFileMd5());
            flag = true;
        } catch (IOException e) {
            // merge failed
            redisTemplate.opsForValue().set(key, "FAILED");
        }
    }
}
