package com.melon.commonservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`user`")
public class User {
    @TableId
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String introduction;
    private String residence;
    private String interest;
    private String gender;
}
