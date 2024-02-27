package com.traffic_project.sns.config.filter;


import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.service.UserService;
import com.traffic_project.sns.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final UserService userService;

    private final String secretKey;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        try {
            if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else if (header == null || !header.startsWith("Bearer ")) {
                log.error("Authorization Header does not start with Bearer {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            } else {
                token = header.split(" ")[1].trim();
            }

            String userName = JwtTokenUtils.getUsername(token, secretKey);
            UserDto userDetails = userService.loadUserByUserName(userName);

            if (!JwtTokenUtils.validate(token, userDetails.getUsername(), secretKey)) {
                filterChain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

    }
}
