package com.traffic_project.sns.dto.request;

public record UserJoinRequest(
        String name,
        String password
) {

    public static UserJoinRequest of(String userName, String password) {
        return new UserJoinRequest(userName, password);
    }
}
