package com.traffic_project.sns.domain.dto;

import com.traffic_project.sns.domain.entity.PostEntity;

import java.sql.Timestamp;

public record PostDto (
        Integer id,
        String title,
        String body,
        UserDto user,
        Timestamp registeredAt,
        Timestamp updatedAt,
        Timestamp removedAt
){
    public static PostDto from(PostEntity entity){
        return new PostDto(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                UserDto.from(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt());
    }

}
