package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.fixture.TestInfoFixture;
import com.traffic_project.sns.fixture.UserEntityFixture;
import com.traffic_project.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    public void givenUserInfo_whenJoining_thenReturnsSuccess() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(bCryptPasswordEncoder.encode(fixture.getPassword())).thenReturn("password_encrypt");
        when(userRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), "password_encrypt")));

        //then
        assertDoesNotThrow(() -> userService.join(fixture.getUserName(), fixture.getPassword()));
    }

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

    @Test
    void givenUserInfo_whenLoggingIn_thenReturnsSuccess() {
        //given
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //when
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());

        //then
        assertDoesNotThrow(() -> userService.login(fixture.getUserName(), fixture.getPassword()));

    }

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







}
