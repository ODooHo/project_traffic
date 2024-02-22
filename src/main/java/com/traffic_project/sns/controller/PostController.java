package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.dto.request.PostModifyRequest;
import com.traffic_project.sns.dto.request.PostWriteRequest;
import com.traffic_project.sns.dto.response.PostResponse;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.service.PostService;
import com.traffic_project.sns.util.ClassUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{postId}")
    public ResponseDto<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication){
        UserDto user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);
        return ResponseDto.success(
                PostResponse.from(postService.modify(user.id(),postId,request.title(), request.body()))
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseDto<Void> delete(@PathVariable Integer postId, Authentication authentication){
        UserDto user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);
        postService.delete(user.id(),postId);
        return ResponseDto.success();
    }


}
