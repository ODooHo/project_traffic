package com.traffic_project.sns.config;

import com.traffic_project.sns.config.filter.JwtAuthenticationFilter;
import com.traffic_project.sns.exception.CustomAuthenticationEntryPoint;
import com.traffic_project.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String secretKey;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) ->{
            web.ignoring()
                    .requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations()
                    )
                    .requestMatchers(
                            "/api/*/users/join",
                            "/api/*/users/login");
        };
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement)
                        -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeHttpRequests)
                        -> authorizeHttpRequests
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling((exceptionHandling)
                        -> exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .addFilterBefore(new JwtAuthenticationFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


}
