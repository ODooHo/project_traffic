package com.traffic_project.sns.dto.response;

import com.traffic_project.sns.domain.dto.CommentDto;

import java.sql.Timestamp;

public record CommentResponse (
        Integer id,
        String comment,
        Integer userId,
        String userName,
        Integer postId,
        Timestamp registeredAt,
        Timestamp updatedAt,
        Timestamp removedAt
){
    public static CommentResponse from(CommentDto dto){
        return new CommentResponse(
                dto.id(),
                dto.comment(),
                dto.userId(),
                dto.userName(),
                dto.postId(),
                dto.registeredAt(),
                dto.updatedAt(),
                dto.removedAt()
        );
    }

}
