package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.domain.dto.alarm.AlarmDto;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.dto.response.ResponseDto;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.AlarmRepository;
import com.traffic_project.sns.repository.UserCacheRepository;
import com.traffic_project.sns.repository.UserRepository;
import com.traffic_project.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserCacheRepository redisRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTime;

    public UserDto loadUserByUserName(String userName){
        return redisRepository.getUser(userName).orElseGet(
                () -> userRepository.findByUserName(userName).map(UserDto::from).orElseThrow(
                        () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND)
                ));
    }

    @Transactional
    public UserDto join(String userName, String password) {
        userRepository.findByUserName(userName).ifPresent(
                it -> {
                    throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("name is %s", userName));
                });

        UserEntity userEntity = userRepository.save(UserEntity.of(userName, passwordEncoder.encode(password)));
        return UserDto.from(userEntity);
    }

    public String login(String userName, String password) {
        UserDto savedUser = loadUserByUserName(userName);
        redisRepository.setUser(savedUser);
        if (!passwordEncoder.matches(password, savedUser.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        return JwtTokenUtils.generateAccessToken(userName, secretKey, expiredTime);
    }



    @Transactional
    public Page<AlarmDto> alarmList(Integer userId, Pageable pageable){
        return alarmRepository.findAllByUserId(userId,pageable).map(AlarmDto::from);
    }


}
