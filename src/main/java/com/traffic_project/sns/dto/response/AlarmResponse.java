package com.traffic_project.sns.dto.response;

import com.traffic_project.sns.domain.dto.alarm.AlarmDto;

import java.sql.Timestamp;

public record AlarmResponse(
        Integer id,
        String text,
        Timestamp timestamp,
        Timestamp updatedAt,
        Timestamp removedAt
) {
    public static AlarmResponse from(AlarmDto alarm) {
        return new AlarmResponse(
                alarm.id(),
                alarm.getAlarmText(),
                alarm.registeredAt(),
                alarm.updatedAt(),
                alarm.removedAt()
        );
    }
}
