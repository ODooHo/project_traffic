package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.UserRepository;
import com.traffic_project.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTime;


    @Transactional
    public UserDto join(String userName, String password) {
        userRepository.findByUserName(userName).ifPresent(
                it -> {
                    throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName is %s", userName));
                });

        UserEntity userEntity = userRepository.save(UserEntity.of(userName, passwordEncoder.encode(password)));
        return UserDto.from(userEntity);
    }

    public String login(String userName, String password) {
        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(
                        () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND)

                );

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        return JwtTokenUtils.generateAccessToken(userName, secretKey, expiredTime);
    }

    public UserDto loadUserByUserName(String userName){
        return userRepository.findByUserName(userName).map(UserDto::from).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND)
        );
    }


}
