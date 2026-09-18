package com.melon.searchservice.service;

public interface SearchService {
    Object search(String keyword, String type, long pageNum, long pageSize, String userId);
}
