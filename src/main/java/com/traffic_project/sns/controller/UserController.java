package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.dto.request.UserJoinRequest;
import com.traffic_project.sns.dto.request.UserLoginRequest;
import com.traffic_project.sns.dto.response.AlarmResponse;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.dto.response.UserJoinResponse;
import com.traffic_project.sns.dto.response.UserLoginResponse;
import com.traffic_project.sns.service.AlarmService;
import com.traffic_project.sns.service.UserService;
import com.traffic_project.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AlarmService alarmService;

    @PostMapping("/join")
    public ResponseDto<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        UserDto user = userService.join(request.name(), request.password());
        return ResponseDto.success(UserJoinResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseDto<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.name(), request.password());
        return ResponseDto.success(UserLoginResponse.of(token));
    }

    @GetMapping("/alarm")
    public ResponseDto<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication){
        UserDto user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);
        return ResponseDto.success(userService.alarmList(user.id(),pageable).map(AlarmResponse::from));
    }

    @GetMapping("alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication){
        log.info("subscribe");
        UserDto user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);
        return alarmService.connectNotification(user.id());
    }

}
