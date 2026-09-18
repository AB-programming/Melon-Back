package com.melon.searchservice.remote;

import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.searchservice.pojo.vo.SearchVideoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "videoService", path = "/")
public interface VideoRemote {
    @GetMapping("/searchVideo")
    HttpResult<PageVo<SearchVideoVo>> searchVideo(@RequestParam("keyword") String keyword,
                                                  @RequestParam("pageNum") long pageNum,
                                                  @RequestParam("pageSize") long pageSize);
}
