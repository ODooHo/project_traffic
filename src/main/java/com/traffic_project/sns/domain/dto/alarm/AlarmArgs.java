package com.traffic_project.sns.domain.dto.alarm;

public record AlarmArgs (
        Integer fromUserId,
        Integer targetId
){

    public static AlarmArgs of(Integer fromUserId, Integer targetId){
        return new AlarmArgs(fromUserId, targetId);
    }

}
