package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.dto.request.PostCommentRequest;
import com.traffic_project.sns.dto.request.PostModifyRequest;
import com.traffic_project.sns.dto.request.PostWriteRequest;
import com.traffic_project.sns.dto.response.CommentResponse;
import com.traffic_project.sns.dto.response.PostResponse;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.service.PostService;
import com.traffic_project.sns.util.ClassUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @GetMapping
    public ResponseDto<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return ResponseDto.success(postService.list(pageable).map(PostResponse::from));
    }

    @GetMapping("/my")
    public ResponseDto<Page<PostResponse>> myPosts(Pageable pageable, Authentication authentication) {
        UserDto user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);
        return ResponseDto.success(postService.my(user.id(), pageable).map(PostResponse::from));
    }

    @PostMapping("/{postId}/likes")
    public ResponseDto<Void> like(@PathVariable Integer postId, Authentication authentication){
        postService.like(postId, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/{postId}/likes")
    public ResponseDto<Integer> getLikes(@PathVariable Integer postId, Authentication authentication) {
        return ResponseDto.success(postService.getLikeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseDto<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication){
        postService.comment(postId, authentication.getName(), request.comment());
        return ResponseDto.success();
    }

    @GetMapping("/{postId}/comments")
    public ResponseDto<Page<CommentResponse>> getComments(@PathVariable Integer postId, Pageable pageable){
        return ResponseDto.success(postService.getComments(postId,pageable).map(CommentResponse::from));
    }


}
