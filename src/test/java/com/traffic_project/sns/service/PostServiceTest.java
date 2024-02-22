package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.entity.PostEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.fixture.TestInfoFixture;
import com.traffic_project.sns.fixture.UserEntityFixture;
import com.traffic_project.sns.repository.PostRepository;
import com.traffic_project.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PostServiceTest {

    @Autowired
    PostService postService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostRepository postRepository;

    @DisplayName("올바른 정보 입력시 포스트 생성이 성공한다.")
    @Test
    void givenPostInfo_whenRequestingCreatePost_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));
        assertDoesNotThrow(() -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
    }


    @DisplayName("존재하지않는 유저가 포스트 생성을 요청할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenRequestingCreatePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("존재하지 않는 포스트에 수정 요청할 시 예외를 반환한다.")
    @Test
    void givenNotExistsPost_whenRequestingModifyPost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () ->
                postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("존재하지 않는 유저는 토큰 필터에서 걸러짐")
    @DisplayName("존재하지 않는 유저가 포스트에 수정 요청할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUSer_whenRequestingModifyPost_thenReturnsException() {

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("포스트 수정 시 포스트 작성자와 유저가 일치하지 않으면 예외를 반환한다.")
    @Test
    void givenNotMatchesUserAndWriter_whenRequestingModifyPost_thenReturnsException() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @DisplayName("존재하지않는 포스트에 삭제 요청 시 예외를 반환한다.")
    @Test
    void givenNotExistsPost_whenRequestingDeletePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("존재하지 않는 유저는 토큰 필터에서 걸러짐")
    @DisplayName("존재하지 않는 유저가 삭제 요청 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenRequestingDeletePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("포스트삭제 시 작성자와 유저가 일치하지 않으면 예외를 반환한다.")
    @Test
    void givenNotMatchesUserAndWriter_whenRequestingDeletePost_thenReturnsException() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }


}
