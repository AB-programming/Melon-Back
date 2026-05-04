package com.melon.videoservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentLikeDto {
    @NotBlank(message = "The userId cannot empty!")
    private String userId;
    @NotBlank(message = "The commentId cannot empty!")
    private String commentId;
}
