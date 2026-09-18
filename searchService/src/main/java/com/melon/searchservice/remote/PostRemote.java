package com.melon.searchservice.remote;

import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.searchservice.pojo.vo.SearchPostVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "postService", path = "/")
public interface PostRemote {
    @GetMapping("/searchPost")
    HttpResult<PageVo<SearchPostVo>> searchPost(@RequestParam("keyword") String keyword,
                                                @RequestParam("pageNum") long pageNum,
                                                @RequestParam("pageSize") long pageSize,
                                                @RequestParam(value = "userId", required = false) String userId);
}
