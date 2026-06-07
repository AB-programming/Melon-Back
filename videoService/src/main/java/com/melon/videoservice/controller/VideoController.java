package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.dto.ChunkDto;
import com.melon.videoservice.pojo.vo.VideoVo;
import com.melon.videoservice.service.VideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Set;

@RestController
public class VideoController {
    @Resource
    private VideoService videoService;

    @PostMapping("/createVideo")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    @Validated
    public String createVideo(@RequestParam("picture") MultipartFile picture,
                              @RequestParam("userId") @NotBlank(message = "The userId cannot be empty") String userId,
                              @RequestParam("title") @NotBlank(message = "The title cannot be empty") String title,
                              @RequestParam("description") String description) throws ServerException {
        return videoService.createVideo(picture, userId, title, description);
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

    @PostMapping("/uploadChunk")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Boolean uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("index") Integer index,
            @RequestParam("fileMd5") String fileMd5
    ) throws ServerException {
        return videoService.uploadChunk(chunk, index, fileMd5);
    }

    @PostMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Set<Object> check(@RequestBody ChunkDto chunkDto) throws ServerException {
        return videoService.check(chunkDto.getFileMd5());
    }

    @PostMapping("/merge")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Boolean merge(@RequestBody ChunkDto chunkDto) throws ServerException {
        return videoService.merge(chunkDto.getFileMd5(), chunkDto.getId());
    }

    @GetMapping("/checkMerge/{fileId}")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String checkMerge(@PathVariable("fileId") String fileId) {
        return videoService.checkMergeResult(fileId);
    }
}
