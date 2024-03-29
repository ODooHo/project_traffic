package com.traffic_project.sns.exception;

import com.traffic_project.sns.dto.response.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value());
        response.getWriter().write(ResponseDto.error(ErrorCode.INVALID_TOKEN.name()).toStream());
    }
}