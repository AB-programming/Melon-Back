package com.melon.searchservice.pojo.vo;

import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.commonservice.pojo.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAllVo {
    private PageVo<SearchVideoVo> videos;
    private PageVo<UserVo> users;
    private PageVo<SearchPostVo> posts;
}
