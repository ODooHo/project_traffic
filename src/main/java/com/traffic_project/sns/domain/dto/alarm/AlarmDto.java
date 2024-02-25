package com.traffic_project.sns.domain.dto.alarm;

import com.traffic_project.sns.domain.entity.AlarmEntity;

import java.sql.Timestamp;

public record AlarmDto(
        Integer id,
        AlarmType alarmType,
        AlarmArgs args,
        Timestamp registeredAt,
        Timestamp updateAt,
        Timestamp removedAt
) {

    public String getAlarmText(){
        return alarmType.getAlarmText();
    }

    public static AlarmDto from(AlarmEntity entity){

    }
}
