package com.melon.userservice.controller;

import com.melon.commonservice.exception.ServerException;
import com.melon.commonservice.pojo.vo.UserVo;
import com.melon.userservice.pojo.dto.NewUserDto;
import com.melon.userservice.pojo.dto.UserDto;
import com.melon.userservice.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/{userId}")
    public UserVo getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/uploadAvatar")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public String uploadAvatar(@NonNull @RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) throws ServerException {
        return userService.uploadAvatar(file, userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public UserVo updateUser(@PathVariable @Validated @NotBlank(message = "The userId cannot be empty") String userId,
                             @RequestBody UserDto userDto) throws ServerException {
        return userService.updateUser(userId, userDto);
    }

    @PostMapping("/createUser")
    public UserVo createUser(@RequestBody @Validated NewUserDto newUserDto) throws ServerException {
        return userService.createUser(newUserDto.getUsername(), newUserDto.getPassword());
    }

    @GetMapping("/getUserListByIds")
    public Map<String, UserVo> getUserListByIds(@RequestParam("ids") List<String> ids) {
        return userService.getUserListByIds(ids);
    }
}
