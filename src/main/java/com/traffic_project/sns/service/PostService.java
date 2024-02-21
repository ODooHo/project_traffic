package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.PostDto;
import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.domain.entity.PostEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.PostRepository;
import com.traffic_project.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public void create(String userName, String title, String body) {
        UserEntity userEntity = userRepository.findByUserName(userName).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND)
        );

        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        postRepository.save(postEntity);
    }

    public String modify(Integer userId, Integer postId, String title, String body) {
        return "Post";
    }

    public void delete(Integer userId, Integer postId) {

    }
}
