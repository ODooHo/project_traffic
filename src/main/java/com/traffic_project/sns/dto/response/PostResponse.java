package com.traffic_project.sns.dto.response;


import com.traffic_project.sns.domain.dto.PostDto;

import java.sql.Timestamp;

public record PostResponse(
        Integer id,
        String title,
        String body,
        UserResponse user,
        Timestamp registeredAt,
        Timestamp updatedAt
) {
    public static PostResponse from(PostDto dto) {
        return new PostResponse(
                dto.id(),
                dto.title(),
                dto.body(),
                UserResponse.from(dto.user()),
                dto.registeredAt(),
                dto.updatedAt()
        );
    }
}
