package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.vo.VideoVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Set;

public interface VideoService {
    String createVideo(MultipartFile picture, String userId, String title, String description) throws ServerException;
    ResponseEntity<StreamingResponseBody> getVideo(String videoId, HttpServletResponse response, String rangeHeader) throws ServerException;
    StreamingResponseBody getCover(String videoId, HttpServletResponse response) throws ServerException;
    List<VideoVo> selectAllVideo() throws ServerException;
    VideoVo getVideoInfoById(String videoId) throws ServerException;
    List<VideoVo> selectVideoListByUserId(String userId) throws ServerException;
    Boolean uploadChunk(MultipartFile chunk, Integer index, String fileMd5) throws ServerException;
    Set<Object> check(String fileMd5);
    Boolean merge(String fileMd5, String id);
    String checkMergeResult(String fileId);
    Boolean deleteVideo(String videoId);
}
