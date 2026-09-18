package com.melon.searchservice.pojo.vo;

import com.melon.commonservice.pojo.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchVideoVo {
    private String id;
    private String title;
    private String description;
    private UserVo author;
}
