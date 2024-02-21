package com.traffic_project.sns.controller;

import com.traffic_project.sns.dto.request.PostWriteRequest;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseDto<Void> create(@RequestBody PostWriteRequest request, Authentication authentication) {
        postService.create(authentication.getName(), request.title(), request.body());
        return ResponseDto.success();
    }


}
