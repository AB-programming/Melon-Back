package com.melon.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.commonservice.pojo.entity.User;
import com.melon.userservice.mapper.UserMapper;
import com.melon.userservice.pojo.dto.UserDto;
import com.melon.userservice.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Value("${uploadLocation}")
    private String uploadLocation;

    @Value("${staticUrl}")
    private String staticUrl;

    @Resource
    private UserMapper userMapper;

    @Override
    public String uploadAvatar(MultipartFile file, String userId) throws ServerException {
        User user = userMapper.selectById(userId);
        String originUrl = user.getAvatarUrl();
        String originFilename = StringUtils.getFilename(originUrl);
        // Delete the original file
        FileSystemUtils.deleteRecursively(new File(uploadLocation + "avatar/" + originFilename));

        String fileType = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String loadPosition = uploadLocation + "avatar/" + userId + "." + fileType;
        try {
            file.transferTo(new File(loadPosition));
        } catch (IOException e) {
            throw new ServerException("File transfer errors");
        }
        String newUrl = staticUrl + "avatar/" + userId + "." + fileType;
        user.setAvatarUrl(newUrl);
        if (userMapper.updateById(user) > 0) {
            // Update user's avatar success
            return newUrl;
        }
        throw new ServerException("Data update failed");
    }

    @Override
    public UserVo getUserById(String userId) {
        User user = userMapper.selectById(userId);
        return UserVo.builder()
                .id(user.getId())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .signature(user.getSignature())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .residence(user.getResidence())
                .gender(user.getGender())
                .interest(user.getInterest())
                .build();
    }

    @Override
    public UserVo updateUser(String userId, UserDto userDto) throws ServerException {
        User user = userMapper.selectById(userId);
        if (Objects.isNull(user)) {
            throw new ServerException("User does not exist");
        }
        user.setNickname(userDto.getNickname());
        user.setSignature(userDto.getSignature());
        user.setIntroduction(userDto.getIntroduction());
        user.setResidence(userDto.getResidence());
        user.setGender(userDto.getGender());
        user.setInterest(userDto.getInterest());
        if (userMapper.updateById(user) <= 0) {
            throw new ServerException("Update failed, please try again later");
        }
        return UserVo.builder()
                .id(user.getId())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .signature(user.getSignature())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .residence(user.getResidence())
                .gender(user.getGender())
                .interest(user.getInterest())
                .build();
    }

    @Override
    public UserVo createUser(String username, String password) throws ServerException {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username);
        Long selectedUserNum = userMapper.selectCount(userLambdaQueryWrapper);
        if (selectedUserNum > 0) {
            throw new ServerException("The user already exists");
        }
        String id = UUID.randomUUID().toString();
        User user = User.builder()
                .id(id)
                .username(username)
                .password(password)
                .build();
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new ServerException("Insert failed, please try again later");
        }
        return UserVo.builder()
                .id(id)
                .username(user.getUsername())
                .build();
    }
}
