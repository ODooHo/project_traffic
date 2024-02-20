package com.traffic_project.sns.dto.response;

public record UserLoginResponse(
        String token
) {
    public static UserLoginResponse of(String token) {
        return new UserLoginResponse(token);
    }
}
