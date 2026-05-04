package com.melon.userservice.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserDto {
    @NotBlank(message = "The username cannot empty!")
    private String username;
    @NotBlank(message = "The password cannot empty!")
    private String password;
}
