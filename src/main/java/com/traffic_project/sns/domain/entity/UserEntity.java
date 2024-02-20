package com.traffic_project.sns.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class UserEntity {
    @Id
    private Integer id;
    @Column(name="user_name")
    private String userName;
}
