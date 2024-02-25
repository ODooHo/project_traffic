package com.traffic_project.sns.service;

import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.fixture.TestInfoFixture;
import com.traffic_project.sns.fixture.UserEntityFixture;
import com.traffic_project.sns.repository.AlarmRepository;
import com.traffic_project.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("비즈니스로직 - 회원가입과 로그인")
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AlarmRepository alarmRepository;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @DisplayName("회원 정보를 입력하면 회원가입에 성공한다.")
    @Test
    public void givenUserInfo_whenJoining_thenReturnsSuccess() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(fixture.getPassword())).thenReturn("password_encrypt");
        when(userRepository.save(any())).thenReturn(UserEntityFixture.get(fixture.getUserName(), "password_encrypt"));

        //then
        assertDoesNotThrow(() -> userService.join(fixture.getUserName(), fixture.getPassword()));
    }

    @DisplayName("중복된 정보를 입력하면 회원가입에 실패한다.")
    @Test
    void givenDuplicatedInfo_whenJoining_thenReturnsException() {

        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));

        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class,
                () -> userService.join(fixture.getUserName(), fixture.getPassword()));

        //then
        assertEquals(ErrorCode.DUPLICATED_USER_NAME, exception.getErrorCode());
    }

    @DisplayName("회원 정보를 입력하면 로그인에 성공한다.")
    @Test
    void givenUserInfo_whenLoggingIn_thenReturnsSuccess() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);
        //then
        assertDoesNotThrow(() -> userService.login(fixture.getUserName(), fixture.getPassword()));

    }

    @DisplayName("존재하지않는 회원 정보를 입력하면 로그인에 실패한다.")
    @Test
    void givenNotExistsUser_whenLoggingIn_thenReturnsException() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class
                , () -> userService.login(fixture.getUserName(), fixture.getPassword()));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("틀린 비밀번호를 입력하면 로그인에 실패한다.")
    @Test
    void givenNotCorrectPassword_whenLoggingIn_thenReturnsException() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), "password1")));
        when(bCryptPasswordEncoder.matches(fixture.getPassword(), "password1")).thenReturn(false);

        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class
                , () -> userService.login(fixture.getUserName(), fixture.getPassword()));

        //then
        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @DisplayName("알람 리스트를 요청하면 성공한다.")
    @Test
    void givenNothing_whenGetAlarmList_thenReturnsSuccess(){
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Pageable pageable = mock(Pageable.class);
        //when
        when(alarmRepository.findAllByUserId(fixture.getUserId(),pageable)).thenReturn(Page.empty());
        //then
        assertDoesNotThrow(() -> userService.alarmList(fixture.getUserId(),pageable));
    }







}
