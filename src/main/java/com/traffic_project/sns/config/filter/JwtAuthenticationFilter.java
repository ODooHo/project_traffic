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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")){
            log.error("Error occurs while getting header");
            filterChain.doFilter(request,response);
            return;
        }

        try{
            final String token = header.split(" ")[1].trim();

            if(JwtTokenUtils.isTokenExpired(token,secretKey)){
                log.error("Key is expired");
                filterChain.doFilter(request,response);
                return;
            }
            String userName = JwtTokenUtils.getUsername(token,secretKey);
            UserDto user = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,null, user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            log.error("Error occurs while validating. {} ",e.toString());
            filterChain.doFilter(request,response);
            return;
        }

        filterChain.doFilter(request,response);
    }
}
