package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.User;
import com.traffic_project.sns.dto.request.UserJoinRequest;
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
    public User join(@RequestBody UserJoinRequest request){
        return userService.join(request.userName(), request.password());
    }

}
