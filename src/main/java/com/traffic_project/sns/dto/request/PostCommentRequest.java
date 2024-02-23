package com.traffic_project.sns.dto.request;

public record PostCommentRequest(
        String comment
)
{
    public static PostCommentRequest of(String comment){
        return new PostCommentRequest(comment);
    }
}
