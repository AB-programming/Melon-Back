package com.melon.videoservice.pojo.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDto {
    private String type;
    private String targetId;
    private String commentId;
    private String content;
}
