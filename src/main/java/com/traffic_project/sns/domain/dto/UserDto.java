package com.traffic_project.sns.domain.dto;

import com.traffic_project.sns.domain.UserRole;
import com.traffic_project.sns.domain.entity.UserEntity;

import java.sql.Timestamp;

public record UserDto (
         Integer id,
         String username,
         String password,
         UserRole role,
         Timestamp registeredAt,
         Timestamp updatedAt,
         Timestamp removedAt
){

    public static UserDto from(UserEntity entity){
        return new UserDto(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt()
        );
    }




}
