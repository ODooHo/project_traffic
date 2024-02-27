package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.dto.alarm.AlarmArgs;
import com.traffic_project.sns.domain.dto.alarm.AlarmNoti;
import com.traffic_project.sns.domain.dto.alarm.AlarmType;
import com.traffic_project.sns.domain.entity.AlarmEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.repository.AlarmRepository;
import com.traffic_project.sns.repository.EmitterRepository;
import com.traffic_project.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {


    private final static String ALARM_NAME = "alarm";

    private final AlarmRepository alarmRepository;
    private final EmitterRepository emitterRepository;
    private final UserRepository userRepository;

    public void send(AlarmType type, AlarmArgs args, Integer receiverId) {
        UserEntity userEntity = userRepository.findById(receiverId).orElseThrow();
        AlarmEntity alarmEntity = AlarmEntity.of(type, args, userEntity);
        alarmRepository.save(alarmEntity);
        emitterRepository.get(receiverId).ifPresentOrElse(it -> {
                    try {
                        it.send(SseEmitter.event()
                                .id(alarmEntity.getId().toString())
                                .name(ALARM_NAME)
                                .data(new AlarmNoti())
                        );
                    } catch (IOException exception) {
                        emitterRepository.delete(receiverId);
                        throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
                    }
                },
                () -> log.info("No emitter founded")
        );
    }

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter connectNotification(Integer userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);
        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            log.info("send");
            emitter.send(SseEmitter.event()
                    .id("id")
                    .name(ALARM_NAME)
                    .data("connect Completed")
            );
        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
        }
        return emitter;
    }
}
