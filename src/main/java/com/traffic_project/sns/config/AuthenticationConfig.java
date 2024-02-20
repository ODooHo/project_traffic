package com.traffic_project.sns.config;

import com.traffic_project.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;


    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity)throws Exception{
        httpSecurity.
                csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement)
                -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeHttpRequests)
                ->authorizeHttpRequests
                                .requestMatchers(
                                        "/api/*/users/join",
                                        "/api/*/users/login").permitAll()
                                .anyRequest().authenticated()
                        );

        return httpSecurity.build();
    }


}
