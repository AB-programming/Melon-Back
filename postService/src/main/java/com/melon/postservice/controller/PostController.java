package com.melon.postservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.commonservice.util.SecurityUtils;
import com.melon.postservice.pojo.vo.PostVo;
import com.melon.postservice.service.PostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
public class PostController {
    @Resource
    private PostService postService;

    @GetMapping("{postId}")
    public PostVo getPostById(@PathVariable String postId) throws ServerException {
        return postService.getPostById(postId);
    }

    @GetMapping("/getPostByIdWithUserId")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public PostVo getPostByIdWithUserId(@RequestParam("postId") String postId) throws ServerException {
        return postService.getPostByIdWithUserId(postId, SecurityUtils.getCurrentUserId());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public PostVo addPost(@RequestParam String content, @RequestParam MultipartFile[] images) throws ServerException {
        return postService.addPost(SecurityUtils.getCurrentUserId(), content, images);
    }

    @GetMapping("/image")
    public StreamingResponseBody getImage(@RequestParam("image") String image, HttpServletResponse response) throws ServerException {
        return postService.getPostImage(image, response);
    }

    @GetMapping("/selectAllPost")
    public List<PostVo> selectAllPost() {
        return postService.selectAllPost();
    }

    @GetMapping("/selectPostListWithUserId")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public List<PostVo> selectPostListWithUserId(@RequestParam("userId") String userId) {
        return postService.selectPostListWithUserId(userId);
    }

    @GetMapping("/selectFollowedPosts")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public List<PostVo> selectFollowedPosts() throws ServerException {
        return postService.selectFollowedPosts(SecurityUtils.getCurrentUserId());
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deletePost(@PathVariable String postId) throws ServerException {
        postService.deletePost(postId, SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/searchPost")
    public PageVo<PostVo> searchPost(@RequestParam("keyword") String keyword,
                                     @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") long pageSize) {
        return postService.searchPost(keyword, pageNum, pageSize, SecurityUtils.getCurrentUserIdOrNull());
    }
}