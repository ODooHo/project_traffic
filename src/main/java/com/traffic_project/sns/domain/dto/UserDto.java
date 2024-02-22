package com.traffic_project.sns.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.traffic_project.sns.domain.UserRole;
import com.traffic_project.sns.domain.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(Integer id, String username, String password, UserRole role, Timestamp registeredAt,
                      Timestamp updatedAt, Timestamp removedAt) implements UserDetails {

    public static UserDto from(UserEntity entity) {
        return new UserDto(entity.getId(), entity.getUserName(), entity.getPassword(), entity.getRole(), entity.getRegisteredAt(), entity.getUpdatedAt(), entity.getRemovedAt());
    }


    public static UserDto of(Integer id, String username, String password, UserRole role, Timestamp registeredAt, Timestamp updatedAt, Timestamp removedAt) {
        return new UserDto(id, username, password, role, registeredAt, updatedAt, removedAt);
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return removedAt == null;
    }


}
