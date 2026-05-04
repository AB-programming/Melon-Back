package com.melon.userservice.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String nickname;
    private String signature;
    private String introduction;
    private String residence;
    private String interest;
    private String gender;
}
