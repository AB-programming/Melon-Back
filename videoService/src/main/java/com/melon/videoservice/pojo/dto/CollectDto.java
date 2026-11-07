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
public class CollectDto {
    @NotBlank(message = "The videoId cannot empty!")
    private String videoId;
}
