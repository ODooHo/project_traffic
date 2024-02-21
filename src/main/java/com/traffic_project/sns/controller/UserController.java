package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.dto.request.UserJoinRequest;
import com.traffic_project.sns.dto.request.UserLoginRequest;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.dto.response.UserJoinResponse;
import com.traffic_project.sns.dto.response.UserLoginResponse;
import com.traffic_project.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseDto<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        UserDto user = userService.join(request.userName(), request.password());
        return ResponseDto.success(UserJoinResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseDto<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.userName(), request.password());
        return ResponseDto.success(UserLoginResponse.of(token));
    }


}
