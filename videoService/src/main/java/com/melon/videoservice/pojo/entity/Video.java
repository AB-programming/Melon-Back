package com.melon.videoservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`video`")
public class Video {
    @TableId
    private String id;
    private String userId;
    private String videoPath;
    private String picturePath;
    private String title;
    private String description;
}
