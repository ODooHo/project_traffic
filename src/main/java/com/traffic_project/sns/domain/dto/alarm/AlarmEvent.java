package com.traffic_project.sns.domain.dto.alarm;

public record AlarmEvent(
        AlarmType type,
        AlarmArgs args,
        Integer receiverUserId
) {
    public static AlarmEvent of(AlarmType alarmType, AlarmArgs args, Integer receiverUserId){
        return new AlarmEvent(alarmType,args,receiverUserId);
    }
}
