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
    public static PostResponse from(PostDto post) {
        return new PostResponse(
                post.id(),
                post.title(),
                post.body(),
                UserResponse.from(post.user()),
                post.registeredAt(),
                post.updatedAt()
        );
    }
}
