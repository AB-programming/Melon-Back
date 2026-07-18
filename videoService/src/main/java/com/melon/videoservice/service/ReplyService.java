package com.melon.videoservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.videoservice.pojo.vo.ReplyVo;

public interface ReplyService {
    ReplyVo addReply(String userId, String type, String targetId, String commentId, String content) throws ServerException;

    Boolean exists(String userId, String type, String targetId);

    void deleteReplyByReplyId(String replyId) throws ServerException;
}
