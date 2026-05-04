package com.melon.videoservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyVo {
    private String id;
    private UserVo user;
    private String targetId;
    private String type;
    private String content;
    private String createdTime;
}
