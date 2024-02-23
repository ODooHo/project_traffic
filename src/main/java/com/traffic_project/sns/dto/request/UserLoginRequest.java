package com.traffic_project.sns.dto.request;

public record UserLoginRequest(
        String name,
        String password
) {

    public static UserLoginRequest of(String userName, String password) {
        return new UserLoginRequest(userName, password);
    }
}
