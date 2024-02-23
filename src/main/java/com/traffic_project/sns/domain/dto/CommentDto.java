package com.traffic_project.sns.domain.dto;

import com.traffic_project.sns.domain.entity.CommentEntity;

import java.sql.Timestamp;

public record CommentDto(
        Integer id,
        String comment,
        Integer userId,
        String userName,
        Integer postId,
        Timestamp registeredAt,
        Timestamp updatedAt,
        Timestamp removedAt
) {
    public static CommentDto from(CommentEntity entity){
        return new CommentDto(
                entity.getId(),
                entity.getComment(),
                entity.getUser().getId(),
                entity.getUser().getUserName(),
                entity.getPost().getId(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt());
    }
}
