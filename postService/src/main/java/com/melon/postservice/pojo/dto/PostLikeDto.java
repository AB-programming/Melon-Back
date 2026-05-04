package com.melon.postservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostLikeDto {
    @NotBlank(message = "The userId cannot empty!")
    private String userId;
    @NotBlank(message = "The postId cannot empty!")
    private String postId;
}
