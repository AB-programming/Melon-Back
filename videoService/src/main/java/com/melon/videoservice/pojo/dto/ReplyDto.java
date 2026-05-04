package com.melon.videoservice.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyDto {
    private String userId;
    private String type;
    private String targetId;
    private String content;
}
