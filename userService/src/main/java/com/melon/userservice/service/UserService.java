package com.melon.userservice.service;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.pojo.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String uploadAvatar(MultipartFile file, String userId) throws ServerException;

    UserVo getUserById(String userId);

    UserVo updateUser(String userId, UserDto userDto) throws ServerException;

    UserVo createUser(String username, String password) throws ServerException;
}
