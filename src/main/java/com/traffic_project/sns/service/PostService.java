package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.PostDto;
import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService
{
    private final PostRepository postRepository;

    public void create(String userName, String title, String body){

    }

    public String modify(Integer userId, Integer postId, String title, String body){
        return "Post";
    }

    public void delete(Integer userId, Integer postId){

    }
}
