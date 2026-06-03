package com.melon.videoservice.pojo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergeMessage {
    private String fileMd5;
    private String fileId;
    private LocalDateTime sendTime;
}
