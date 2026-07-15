package com.melon.videoservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`reply`")
public class Reply {
    @TableId
    private String id;
    private String userId;
    private String targetId;
    private String targetUserId;
    private String commentId;
    private String type;
    private String content;
    private LocalDateTime createdTime;
}
