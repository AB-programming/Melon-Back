package com.melon.postservice.remote;

import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.pojo.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "userService", path = "/")
public interface UserRemote {
    @GetMapping("/{userId}")
    HttpResult<UserVo> getUserById(@PathVariable("userId") String userId);

    @GetMapping("/subscription/getMySubscriptions")
    HttpResult<List<UserVo>> getMySubscriptions(@RequestParam("subscriber") String subscriber);
}
