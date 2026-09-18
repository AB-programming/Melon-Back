package com.melon.searchservice.controller;

import com.melon.searchservice.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Resource
    private SearchService searchService;

    @GetMapping("/")
    public Object search(@RequestParam("keyword") String keyword,
                         @RequestParam(value = "type", defaultValue = "all") String type,
                         @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
                         @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
                         @RequestParam(value = "userId", required = false) String userId) {
        return searchService.search(keyword, type, pageNum, pageSize, userId);
    }
}
