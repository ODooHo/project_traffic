package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.PostDto;
import com.traffic_project.sns.domain.entity.PostEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.PostRepository;
import com.traffic_project.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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

    public PostDto modify(Integer userId, Integer postId, String title, String body) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(
                () -> new SnsApplicationException(
                        ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)
                ));

        if(!Objects.equals(postEntity.getUser().getId(),userId)){
            throw new SnsApplicationException(
                    ErrorCode.INVALID_PERMISSION,
                    String.format("user %s has no permission with post %d", userId, postId
                    ));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);
        return PostDto.from(postRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(Integer userId, Integer postId) {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        if (!Objects.equals(postEntity.getUser().getId(), userId)) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("user %s has no permission with post %d", userId, postId));
        }
        postRepository.delete(postEntity);
    }
}
