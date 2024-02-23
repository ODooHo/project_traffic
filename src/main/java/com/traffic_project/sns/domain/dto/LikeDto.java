package com.traffic_project.sns.domain.dto;

import com.traffic_project.sns.domain.entity.LikeEntity;

import java.sql.Timestamp;

public record LikeDto(
        Integer id,
        Integer userId,
        String userName,
        Integer postId,
        Timestamp registeredAt,
        Timestamp updatedAt,
        Timestamp removedAt
) {

    public static LikeDto from(LikeEntity entity){
        return new LikeDto(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUserName(),
                entity.getPost().getId(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt()
        );
    }
}
