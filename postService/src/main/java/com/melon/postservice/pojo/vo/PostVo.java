package com.melon.postservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostVo {
    private String id;
    private String content;
    private UserVo user;
    private List<String> images;
    private String createdTime;
    private Boolean isLike;
    private Long likeCount;
}
