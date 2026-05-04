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
import com.melon.videoservice.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoMapper videoMapper;

    @Resource
    private HdfsService hdfsService;

    @Resource
    private UserRemote userRemote;

    @Override
    public String createVideo(MultipartFile videoFile, MultipartFile pictureFile, String userId, String title, String description) throws ServerException {
        String videoExtension = StringUtils.getFilenameExtension(videoFile.getOriginalFilename());
        String pictureExtension = StringUtils.getFilenameExtension(pictureFile.getOriginalFilename());
        String id = UUID.randomUUID().toString();
        String videoPath = "/video/" + id + "." + videoExtension;
        String picturePath = "/cover/" + id + "." + pictureExtension;
        Video video = Video.builder()
                .id(id)
                .userId(userId)
                .videoPath(videoPath)
                .picturePath(picturePath)
                .title(title)
                .description(description)
                .build();
        if (videoMapper.insert(video) <= 0) {
            throw new ServerException("The video was not uploaded successfully. Please try again later.");
        }
        try {
            hdfsService.upload(videoPath, videoFile.getInputStream());
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
                while (toRead > 0 && (len = in.read(buf, 0, (int)Math.min(buf.length, toRead))) != -1) {
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

    public List<VideoVo> convertVideoListToVideoVoList(List<Video> videoList) throws ServerException {
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
