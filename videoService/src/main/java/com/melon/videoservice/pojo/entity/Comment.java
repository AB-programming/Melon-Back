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
@TableName("`comment`")
public class Comment {
    @TableId
    private String id;
    private String userId;
    private String videoId;
    private String content;
    private LocalDateTime createdTime;
}
