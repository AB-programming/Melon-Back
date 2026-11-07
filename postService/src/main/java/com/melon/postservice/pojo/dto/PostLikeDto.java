package com.melon.postservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class PostLikeDto {
    @NotBlank(message = "The postId cannot empty!")
    private String postId;
}
