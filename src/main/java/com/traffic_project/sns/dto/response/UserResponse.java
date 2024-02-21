package com.traffic_project.sns.dto.response;

import com.traffic_project.sns.domain.dto.UserDto;

public record UserResponse(
        Integer id,
        String userName
) {

    public static UserResponse from(UserDto user){
        return new UserResponse(
                user.id(),
                user.username()
        );
    }
}
