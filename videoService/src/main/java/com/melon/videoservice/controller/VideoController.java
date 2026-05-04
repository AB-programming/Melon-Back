package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.vo.VideoVo;
import com.melon.videoservice.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
public class VideoController {
    @Resource
    private VideoService videoService;

    @PostMapping("/createVideo")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String createVideo(@RequestParam("video") MultipartFile video,
                              @RequestParam("picture") MultipartFile picture,
                              @RequestParam("userId") String userId,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description) throws ServerException {
        return videoService.createVideo(video, picture, userId, title, description);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<StreamingResponseBody> getVideo(
            @PathVariable("videoId") String videoId,
            HttpServletResponse response,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) throws ServerException {
        return videoService.getVideo(videoId, response, rangeHeader);
    }

    @GetMapping("/cover/{videoId}")
    public StreamingResponseBody getCover(@PathVariable("videoId") String videoId, HttpServletResponse response) throws ServerException {
        return videoService.getCover(videoId, response);
    }

    @GetMapping("/selectAllVideo")
    public List<VideoVo> selectAllVideo() throws ServerException {
        return videoService.selectAllVideo();
    }

    @GetMapping("/getVideoInfo")
    public VideoVo getVideoInfo(@RequestParam("videoId") String videoId) throws ServerException {
        return videoService.getVideoInfoById(videoId);
    }

    @GetMapping("/selectVideoListByUserId")
    public List<VideoVo> selectVideoListByUserId(@RequestParam("userId") String userId) throws ServerException {
        return videoService.selectVideoListByUserId(userId);
    }
}
