package com.traffic_project.sns.dto.request;

public record PostWriteRequest(
        String title,
        String body
) {

    public static PostWriteRequest of(String title, String body) {
        return new PostWriteRequest(title, body);
    }
}
