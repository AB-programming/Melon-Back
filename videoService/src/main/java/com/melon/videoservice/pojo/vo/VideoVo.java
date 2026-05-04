package com.melon.videoservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoVo {
    private String id;
    private String title;
    private UserVo author;
    private String description;
}
