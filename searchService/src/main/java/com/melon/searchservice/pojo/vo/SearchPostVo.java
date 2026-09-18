package com.melon.searchservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostVo {
    private String id;
    private String content;
    private UserVo user;
    private List<String> images;
    private String createdTime;
    private Boolean isLike;
    private Long likeCount;
}
