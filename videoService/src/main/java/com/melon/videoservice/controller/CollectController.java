package com.melon.videoservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.util.SecurityUtils;
import com.melon.videoservice.pojo.dto.CollectDto;
import com.melon.videoservice.service.CollectService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Resource
    private CollectService collectService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String addCollect(@RequestBody @Validated CollectDto collectDto) throws ServerException {
        return collectService.addCollect(SecurityUtils.getCurrentUserId(), collectDto.getVideoId());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public void deleteCollect(@RequestBody @Validated CollectDto collectDto) throws ServerException {
        collectService.deleteCollect(SecurityUtils.getCurrentUserId(), collectDto.getVideoId());
    }

    @GetMapping("/isCollect")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public Boolean isCollect(@RequestParam("videoId") String videoId) throws ServerException {
        return collectService.exists(SecurityUtils.getCurrentUserId(), videoId);
    }
}
