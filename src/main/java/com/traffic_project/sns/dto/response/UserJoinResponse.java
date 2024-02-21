package com.traffic_project.sns.dto.response;

import com.traffic_project.sns.domain.dto.UserDto;

public record UserJoinResponse(
        Integer id,
        String name
) {
    public static UserJoinResponse from(UserDto user) {
        return new UserJoinResponse(user.id(), user.username());
    }
}
