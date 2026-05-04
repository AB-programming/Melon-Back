package com.melon.userservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionDto {
    @NotBlank(message = "The subscriber cannot empty!")
    private String subscriber;
    @NotBlank(message = "The target cannot empty!")
    private String target;
}
