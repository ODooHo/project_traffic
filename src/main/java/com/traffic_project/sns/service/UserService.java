package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.User;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User join(String userName, String password) {
        userRepository.findByUserName(userName).ifPresent(
                it ->{ throw new SnsApplicationException();});

        userRepository.save(new UserEntity());

        return new User();
    }

    public String login(String userName, String password) {
        UserEntity userEntity = userRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException());




        return "login";
    }



}
