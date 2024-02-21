package com.traffic_project.sns.dto.request;

public record PostModifyRequest(
        String title,
        String body
) {

    public static PostModifyRequest of(String title, String body){
        return new PostModifyRequest(title,body);
    }
}
