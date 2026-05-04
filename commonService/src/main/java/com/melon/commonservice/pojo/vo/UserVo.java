package com.melon.commonservice.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    private String id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String introduction;
    private String residence;
    private String interest;
    private String gender;
}
