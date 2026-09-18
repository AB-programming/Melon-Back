package com.melon.searchservice.service.impl;

import com.melon.commonservice.pojo.vo.PageVo;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.searchservice.pojo.vo.SearchAllVo;
import com.melon.searchservice.pojo.vo.SearchPostVo;
import com.melon.searchservice.pojo.vo.SearchVideoVo;
import com.melon.searchservice.remote.PostRemote;
import com.melon.searchservice.remote.UserRemote;
import com.melon.searchservice.remote.VideoRemote;
import com.melon.searchservice.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private static final long ALL_PAGE_NUM = 1;
    private static final long ALL_VIDEO_SIZE = 6;
    private static final long ALL_USER_SIZE = 6;
    private static final long ALL_POST_SIZE = 5;

    @Resource
    private UserRemote userRemote;

    @Resource
    private VideoRemote videoRemote;

    @Resource
    private PostRemote postRemote;

    @Override
    public Object search(String keyword, String type, long pageNum, long pageSize, String userId) {
        if (!StringUtils.hasText(keyword)) {
            return emptyResult(type, pageNum, pageSize);
        }
        return switch (type) {
            case "video" -> videoRemote.searchVideo(keyword, pageNum, pageSize).getData();
            case "user" -> userRemote.searchUser(keyword, pageNum, pageSize).getData();
            case "post" -> postRemote.searchPost(keyword, pageNum, pageSize, userId).getData();
            default -> searchAll(keyword, userId);
        };
    }

    private SearchAllVo searchAll(String keyword, String userId) {
        PageVo<SearchVideoVo> videos = videoRemote.searchVideo(keyword, ALL_PAGE_NUM, ALL_VIDEO_SIZE).getData();
        PageVo<UserVo> users = userRemote.searchUser(keyword, ALL_PAGE_NUM, ALL_USER_SIZE).getData();
        PageVo<SearchPostVo> posts = postRemote.searchPost(keyword, ALL_PAGE_NUM, ALL_POST_SIZE, userId).getData();
        return SearchAllVo.builder()
                .videos(videos)
                .users(users)
                .posts(posts)
                .build();
    }

    private Object emptyResult(String type, long pageNum, long pageSize) {
        return switch (type) {
            case "video" -> this.<SearchVideoVo>emptyPage(pageNum, pageSize);
            case "user" -> this.<UserVo>emptyPage(pageNum, pageSize);
            case "post" -> this.<SearchPostVo>emptyPage(pageNum, pageSize);
            default -> SearchAllVo.builder()
                    .videos(this.emptyPage(ALL_PAGE_NUM, ALL_VIDEO_SIZE))
                    .users(this.emptyPage(ALL_PAGE_NUM, ALL_USER_SIZE))
                    .posts(this.emptyPage(ALL_PAGE_NUM, ALL_POST_SIZE))
                    .build();
        };
    }

    private <T> PageVo<T> emptyPage(long pageNum, long pageSize) {
        return PageVo.<T>builder()
                .records(List.of())
                .total(0)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }
}
