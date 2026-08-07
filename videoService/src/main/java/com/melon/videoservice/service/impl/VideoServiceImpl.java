package com.melon.videoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.baseservice.service.HdfsService;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.videoservice.mapper.VideoMapper;
import com.melon.videoservice.pojo.entity.Video;
import com.melon.videoservice.pojo.vo.VideoVo;
import com.melon.videoservice.remote.UserRemote;
import com.melon.videoservice.service.DeleteVideoProducer;
import com.melon.videoservice.service.MergeProducer;
import com.melon.videoservice.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.IOUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.time.Duration;
import java.util.*;

@Service
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoMapper videoMapper;

    @Resource
    private HdfsService hdfsService;

    @Resource
    private UserRemote userRemote;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MergeProducer mergeProducer;

    @Resource
    private DeleteVideoProducer deleteVideoProducer;

    @Override
    public String createVideo(MultipartFile pictureFile, String userId, String title, String description) throws ServerException {
        String pictureExtension = StringUtils.getFilenameExtension(pictureFile.getOriginalFilename());
        String id = UUID.randomUUID().toString();
        String picturePath = "/cover/" + id + "." + pictureExtension;
        String videoPath = "/video/" + id + ".mp4";
        Video video = Video.builder()
                .id(id)
                .userId(userId)
                .picturePath(picturePath)
                .videoPath(videoPath)
                .title(title)
                .description(description)
                .build();
        if (videoMapper.insert(video) <= 0) {
            throw new ServerException("The video was not uploaded successfully. Please try again later.");
        }
        try {
            hdfsService.upload(picturePath, pictureFile.getInputStream());
        } catch (IOException e) {
            throw new ServerException("Failed to upload the video.");
        }
        return id;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getVideo(String videoId, HttpServletResponse response, String rangeHeader) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video does not exist.");
        }
        String path = video.getVideoPath();
        long length = hdfsService.getLength(path);
        long start = 0, end = length - 1;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring(6).split("-");
            start = Long.parseLong(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
            if (end >= length) {
                end = length - 1;
            }
        }
        long chunkSize = end - start + 1;

        StreamingResponseBody responseBody = getStreamingResponseBody(start, path, chunkSize);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentLength(chunkSize);

        if (rangeHeader != null) {
            headers.set(HttpHeaders.CONTENT_RANGE,
                    String.format("bytes %d-%d/%d", start, end, length));
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(responseBody);
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    private StreamingResponseBody getStreamingResponseBody(long start, String path, long chunkSize) {
        return output -> {
            try (FSDataInputStream in = hdfsService.open(path)) {
                in.seek(start);
                byte[] buf = new byte[8 * 1024];
                long toRead = chunkSize;
                int len;
                while (toRead > 0 && (len = in.read(buf, 0, (int) Math.min(buf.length, toRead))) != -1) {
                    output.write(buf, 0, len);
                    toRead -= len;
                }
            }
        };
    }

    @Override
    public StreamingResponseBody getCover(String videoId, HttpServletResponse response) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            throw new ServerException("The video does not exist.");
        }
        String extension = StringUtils.getFilenameExtension(video.getPicturePath());
        String path = video.getPicturePath();
        long length = hdfsService.getLength(path);
        response.setContentLengthLong(length);
        if (Objects.isNull(extension)) {
            throw new ServerException("The picture path does not exist.");
        }
        switch (extension) {
            case "jpg", "jpeg" -> response.setContentType("image/jpeg");
            case "png" -> response.setContentType("image/png");
            case "gif" -> response.setContentType("image/gif");
        }
        return outputStream -> {
            try (FSDataInputStream in = hdfsService.open(path)) {
                IOUtils.copyBytes(in, outputStream, 8 * 1024, false);
            }
        };
    }

    @Override
    public List<VideoVo> selectAllVideo() throws ServerException {
        List<Video> videoList = videoMapper.selectList(null);
        return this.convertVideoListToVideoVoList(videoList);
    }

    @Override
    public VideoVo getVideoInfoById(String videoId) throws ServerException {
        Video video = videoMapper.selectById(videoId);
        HttpResult<UserVo> userResult = userRemote.getUserById(video.getUserId());
        VideoVo.VideoVoBuilder builder = VideoVo.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription());
        if (userResult.getCode().equals(HttpResponseStatus.OK.getCode())) {
            builder.author(userResult.getData());
        }
        return builder.build();
    }

    @Override
    public List<VideoVo> selectVideoListByUserId(String userId) throws ServerException {
        LambdaQueryWrapper<Video> videoLambdaQueryWrapper = new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId);
        List<Video> videoList = videoMapper.selectList(videoLambdaQueryWrapper);
        return this.convertVideoListToVideoVoList(videoList);
    }

    @Override
    public Boolean uploadChunk(MultipartFile chunk, Integer index, String fileMd5) throws ServerException {
        // Avoid duplicate uploads
        String key = "upload:chunks:" + fileMd5;
        Boolean isUploaded = redisTemplate.opsForSet().isMember(key, index);
        if (Boolean.TRUE.equals(isUploaded)) {
            // chunk has exists
            return true;
        }
        String chunkDir = System.getProperty("user.dir")
                + File.separator
                + "upload-temp"
                + File.separator
                + fileMd5;
        File dir = new File(chunkDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File chunkFile = new File(dir, index + ".part");
        try {
            chunk.transferTo(chunkFile);
            // Add this shard to the cache
            redisTemplate.opsForSet().add(key, index);
            // setup expire
            redisTemplate.expire(key, Duration.ofHours(12));
        } catch (IOException e) {
            throw new ServerException("Chunk upload failed.");
        }
        return true;
    }

    @Override
    public Set<Object> check(String fileMd5) {
        String key = "upload:chunks:" + fileMd5;
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Boolean merge(String fileMd5, String id) {
        String key = "merge:status:" + id;
        redisTemplate.opsForValue().set(key, "MERGING");
        mergeProducer.sendMergeMessage(fileMd5, id);
        return true;
    }

    @Override
    public String checkMergeResult(String fileId) {
        String key = "merge:status:" + fileId;
        String status = (String) redisTemplate.opsForValue().get(key);
        if (Objects.isNull(status) || status.isEmpty()) {
            return "FAILED";
        }
        return status;
    }

    @Override
    public Boolean deleteVideo(String videoId) {
        Video video = videoMapper.selectById(videoId);
        if (Objects.isNull(video)) {
            return false;
        }
        if (videoMapper.deleteById(videoId) <= 0) {
            return false;
        }
        if (StringUtils.hasText(video.getVideoPath()) && StringUtils.hasText(video.getPicturePath())) {
            deleteVideoProducer.sendDeleteMessage(videoId, video.getVideoPath(), video.getPicturePath());
        }
        return true;
    }

    @Override
    public List<VideoVo> selectFollowVideoList(String userId) {
        HttpResult<List<UserVo>> subscriptionsResult = userRemote.getMySubscriptions(userId);
        if (HttpResponseStatus.OK.getCode() == subscriptionsResult.getCode()) {
            List<UserVo> userList = subscriptionsResult.getData();
            List<String> userIds = userList.stream()
                    .map(UserVo::getId)
                    .toList();
            if (!userIds.isEmpty()) {
                LambdaQueryWrapper<Video> videoLambdaQueryWrapper = new LambdaQueryWrapper<>();
                videoLambdaQueryWrapper.in(Video::getUserId, userIds);
                List<Video> videoList = videoMapper.selectList(videoLambdaQueryWrapper);
                return this.convertVideoListToVideoVoList(videoList);
            }
        }
        return List.of();
    }

    public List<VideoVo> convertVideoListToVideoVoList(List<Video> videoList) {
        return videoList.parallelStream()
                .map(video -> {
                    VideoVo.VideoVoBuilder builder = VideoVo.builder()
                            .id(video.getId())
                            .title(video.getTitle())
                            .description(video.getDescription());
                    HttpResult<UserVo> userResult = userRemote.getUserById(video.getUserId());
                    if (userResult.getCode().equals(HttpResponseStatus.OK.getCode())) {
                        builder.author(userResult.getData());
                    }
                    return builder.build();
                }).toList();
    }
}
