package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.vo.CommentVo;

import java.util.List;

public interface CommentService {
    CommentVo addComment(String userId, String videoId, String content) throws ServerException;

    List<CommentVo> getCommentListByUserIdAndVideoId(String userId, String videoId);

    void deleteCommentByCommentId(String commentId) throws ServerException;
}
