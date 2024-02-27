package com.traffic_project.sns.controller;

import com.traffic_project.sns.domain.dto.alarm.AlarmArgs;
import com.traffic_project.sns.domain.dto.alarm.AlarmEvent;
import com.traffic_project.sns.domain.dto.alarm.AlarmType;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.producer.AlarmProducer;
import com.traffic_project.sns.repository.UserRepository;
import com.traffic_project.sns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-dev/v1")
@RequiredArgsConstructor
public class DevController {
    private final AlarmService notificationService;
    private final UserRepository userRepository;
    private final AlarmProducer alarmProducer;

    @GetMapping("/notification")
    public void test() {
        UserEntity entity = userRepository.findById(2).orElseThrow();
        notificationService.send(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(0, 0), entity.getId());
    }

    @GetMapping("/send")
    public void send() {
        alarmProducer.send(new AlarmEvent(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(0, 0), 2));
    }

}
