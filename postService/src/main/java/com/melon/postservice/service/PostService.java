package com.melon.postservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.postservice.pojo.vo.PostVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface PostService {
    PostVo addPost(String userId, String content, MultipartFile[] files) throws ServerException;
    StreamingResponseBody getPostImage(String imageUrl, HttpServletResponse response) throws ServerException;
    List<PostVo> selectAllPost();
    List<PostVo> selectPostListWithUserId(String userId);
    List<PostVo> selectFollowedPosts(String userId) throws ServerException;
    void deletePost(String postId) throws ServerException;
    PostVo getPostById(String postId) throws ServerException;
    PostVo getPostByIdWithUserId(String postId, String userId) throws ServerException;
}
