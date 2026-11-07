package com.melon.videoservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLikeDto {
    @NotBlank(message = "The commentId cannot empty!")
    private String commentId;
}
