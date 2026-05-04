package com.melon.videoservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentVo {
    private String id;
    private UserVo user;
    private String content;
    private String createdTime;
    private Long likeCount;
    private Boolean isLiked;
}
