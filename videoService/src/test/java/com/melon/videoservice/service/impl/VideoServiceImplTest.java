package com.melon.videoservice.service.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private HdfsService hdfsService;

    @Mock
    private UserRemote userRemote;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private MergeProducer mergeProducer;

    @Mock
    private DeleteVideoProducer deleteVideoProducer;

    @InjectMocks
    private VideoServiceImpl videoService;

    private Video video(String id, String userId) {
        return Video.builder()
                .id(id)
                .userId(userId)
                .videoPath("/video/" + id + ".mp4")
                .picturePath("/cover/" + id + ".png")
                .title("title")
                .description("description")
                .build();
    }

    private HttpResult<UserVo> okUser(String id) {
        return HttpResult.<UserVo>builder()
                .code(HttpResponseStatus.OK.getCode())
                .data(UserVo.builder().id(id).username("username").build())
                .build();
    }

    @Test
    void createVideo_success() throws ServerException {
        when(videoMapper.insert(any(Video.class))).thenReturn(1);
        MultipartFile picture = mock(MultipartFile.class);
        when(picture.getOriginalFilename()).thenReturn("cover.png");

        String id = videoService.createVideo(picture, "u1", "title", "description");

        assertThat(id).isNotBlank();
    }

    @Test
    void createVideo_whenInsertFail_throws() {
        when(videoMapper.insert(any(Video.class))).thenReturn(0);
        MultipartFile picture = mock(MultipartFile.class);
        when(picture.getOriginalFilename()).thenReturn("cover.png");

        assertThatThrownBy(() -> videoService.createVideo(picture, "u1", "title", "description"))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video was not uploaded successfully. Please try again later.");
    }

    @Test
    void createVideo_whenUploadIOException_throws() throws IOException {
        when(videoMapper.insert(any(Video.class))).thenReturn(1);
        MultipartFile picture = mock(MultipartFile.class);
        when(picture.getOriginalFilename()).thenReturn("cover.png");
        when(picture.getInputStream()).thenThrow(new IOException("boom"));

        assertThatThrownBy(() -> videoService.createVideo(picture, "u1", "title", "description"))
                .isInstanceOf(ServerException.class)
                .hasMessage("Failed to upload the video.");
    }

    @Test
    void getVideoInfoById_success() throws ServerException {
        when(videoMapper.selectById("v1")).thenReturn(video("v1", "u1"));
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));

        VideoVo result = videoService.getVideoInfoById("v1");

        assertThat(result.getId()).isEqualTo("v1");
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getAuthor().getId()).isEqualTo("u1");
    }

    @Test
    void getVideo_whenNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> videoService.getVideo("v1", null, null))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video does not exist.");
    }

    @Test
    void getCover_whenNotExist_throws() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThatThrownBy(() -> videoService.getCover("v1", null))
                .isInstanceOf(ServerException.class)
                .hasMessage("The video does not exist.");
    }

    @Test
    void deleteVideo_whenNotExist_returnsFalse() {
        when(videoMapper.selectById("v1")).thenReturn(null);

        assertThat(videoService.deleteVideo("v1")).isFalse();
    }

    @Test
    void deleteVideo_whenDeleteFail_returnsFalse() {
        when(videoMapper.selectById("v1")).thenReturn(video("v1", "u1"));
        when(videoMapper.deleteById("v1")).thenReturn(0);

        assertThat(videoService.deleteVideo("v1")).isFalse();
    }

    @Test
    void deleteVideo_success() {
        when(videoMapper.selectById("v1")).thenReturn(video("v1", "u1"));
        when(videoMapper.deleteById("v1")).thenReturn(1);

        assertThat(videoService.deleteVideo("v1")).isTrue();
        verify(deleteVideoProducer).sendDeleteMessage("v1", "/video/v1.mp4", "/cover/v1.png");
    }

    @Test
    void selectAllVideo_success() throws ServerException {
        when(videoMapper.selectList(any())).thenReturn(List.of(video("v1", "u1")));
        when(userRemote.getUserById("u1")).thenReturn(okUser("u1"));

        List<VideoVo> result = videoService.selectAllVideo();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("v1");
    }

    @Test
    void selectFollowVideoList_whenNoSubscriptions_returnsEmpty() {
        when(userRemote.getMySubscriptions("u1")).thenReturn(
                HttpResult.<List<UserVo>>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(List.of())
                        .build());

        assertThat(videoService.selectFollowVideoList("u1")).isEmpty();
    }

    @Test
    void selectFollowVideoList_success() {
        when(userRemote.getMySubscriptions("u1")).thenReturn(
                HttpResult.<List<UserVo>>builder()
                        .code(HttpResponseStatus.OK.getCode())
                        .data(List.of(UserVo.builder().id("u2").build()))
                        .build());
        when(videoMapper.selectList(any())).thenReturn(List.of(video("v1", "u2")));
        when(userRemote.getUserById("u2")).thenReturn(okUser("u2"));

        List<VideoVo> result = videoService.selectFollowVideoList("u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor().getId()).isEqualTo("u2");
    }

    @Test
    void checkMergeResult_whenNull_returnsFailed() {
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        assertThat(videoService.checkMergeResult("file1")).isEqualTo("FAILED");
    }

    @Test
    void checkMergeResult_whenMerging_returnsStatus() {
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("MERGING");

        assertThat(videoService.checkMergeResult("file1")).isEqualTo("MERGING");
    }

    @Test
    void merge_returnsTrue() {
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        assertThat(videoService.merge("md5", "file1")).isTrue();
        verify(mergeProducer).sendMergeMessage("md5", "file1");
    }
}
