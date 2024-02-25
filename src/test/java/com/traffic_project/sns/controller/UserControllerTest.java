package com.traffic_project.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic_project.sns.domain.dto.UserDto;
import com.traffic_project.sns.dto.request.UserJoinRequest;
import com.traffic_project.sns.dto.request.UserLoginRequest;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller 테스트")
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("올바른 회원가입 요청이 오면 성공 응답을 반환한다.")
    @Test
    @WithAnonymousUser
    public void givenUserInfo_whenRequestingJoin_thenReturnsOk() throws Exception {
        //given
        String userName = "dooho";
        String password = "1234";

        //when&then
        when(userService.join(userName, password)).thenReturn(mock(UserDto.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserJoinRequest.of(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("이미 존재하는 회원정보로 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    public void givenExistingUserInfo_whenRequestingJoin_thenReturnsException() throws Exception {
        //given
        String userName = "dooho";
        String password = "1234";

        //when&then
        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME));


        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserJoinRequest.of(userName, password)))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USER_NAME.getStatus().value()));

    }


    @DisplayName("올바른 로그인 요청이 오면 성공 응답을 반환한다.")
    @Test
    @WithAnonymousUser
    public void givenCorrectUserInfo_whenRequestingLogin_thenReturnsSuccess() throws Exception {
        //given
        String userName = "name";
        String password = "password";

        //when&then
        when(userService.login(userName, password)).thenReturn("testToken");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("존재하지 않는 정보로 로그인 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    public void givenNotExistsUser_whenRequestingLogin_thenReturnsException() throws Exception {
        //given
        String userName = "name";
        String password = "password";

        //when&then
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("틀린 정보로 로그인 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    public void givenWrongUserInfo_whenRequestingLogin_thenReturnsException() throws Exception {
        //given
        String userName = "name";
        String password = "password";

        //when&then
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("알람 페이지를 호출하면 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingAlarmList_thenReturnsSuccess() throws Exception {
        //when&then
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 알람 페이지를 호출하면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingAlarmList_thenReturnSuccess() throws Exception {
        //when&then
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
