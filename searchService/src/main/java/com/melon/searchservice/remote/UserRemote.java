package com.melon.searchservice.remote;

import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.commonservice.pojo.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "userService", path = "/")
public interface UserRemote {
    @GetMapping("/searchUser")
    HttpResult<PageVo<UserVo>> searchUser(@RequestParam("keyword") String keyword,
                                          @RequestParam("pageNum") long pageNum,
                                          @RequestParam("pageSize") long pageSize);
}
