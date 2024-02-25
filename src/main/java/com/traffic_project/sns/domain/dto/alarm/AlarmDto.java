package com.traffic_project.sns.domain.dto.alarm;

import com.traffic_project.sns.domain.entity.AlarmEntity;

import java.sql.Timestamp;

public record AlarmDto(
        Integer id,
        AlarmType alarmType,
        AlarmArgs args,
        Timestamp registeredAt,
        Timestamp updatedAt,
        Timestamp removedAt
) {

    public String getAlarmText(){
        return alarmType.getAlarmText();
    }

    public static AlarmDto from(AlarmEntity entity){
        return new AlarmDto(entity.getId(),
                entity.getAlarmType(),
                entity.getArgs(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt()
        );
    }
}
