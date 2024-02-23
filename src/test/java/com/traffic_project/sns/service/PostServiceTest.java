package com.traffic_project.sns.service;

import com.traffic_project.sns.domain.entity.CommentEntity;
import com.traffic_project.sns.domain.entity.LikeEntity;
import com.traffic_project.sns.domain.entity.PostEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.fixture.TestInfoFixture;
import com.traffic_project.sns.fixture.UserEntityFixture;
import com.traffic_project.sns.repository.CommentRepository;
import com.traffic_project.sns.repository.LikeRepository;
import com.traffic_project.sns.repository.PostRepository;
import com.traffic_project.sns.repository.UserRepository;
import org.apache.zookeeper.Op;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @MockBean
    LikeRepository likeRepository;

    @MockBean
    CommentRepository commentRepository;

    @DisplayName("올바른 정보 입력시 포스트 생성이 성공한다.")
    @Test
    void givenPostInfo_whenCreatePost_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));
        assertDoesNotThrow(() -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
    }


    @DisplayName("존재하지않는 유저가 포스트 생성할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenCreatePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mock(PostEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("존재하지 않는 포스트를 수정할 시 예외를 반환한다.")
    @Test
    void givenNotExistsPost_whenModifyPost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () ->
                postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("존재하지 않는 유저는 토큰 필터에서 걸러짐")
    @DisplayName("존재하지 않는 유저가 포스트에 수정 요청할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUSer_whenModifyPost_thenReturnsException() {

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("포스트 수정 시 포스트 작성자와 유저가 일치하지 않으면 예외를 반환한다.")
    @Test
    void givenNotMatchesUserAndWriter_whenModifyPost_thenReturnsException() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @DisplayName("존재하지않는 포스트를 삭제 시 예외를 반환한다.")
    @Test
    void givenNotExistsPost_whenDeletePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("존재하지 않는 유저는 토큰 필터에서 걸러짐")
    @DisplayName("존재하지 않는 유저가 삭제 요청 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenDeletePost_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("포스트삭제 시 작성자와 유저가 일치하지 않으면 예외를 반환한다.")
    @Test
    void givenNotMatchesUserAndWriter_whenDeletePost_thenReturnsException() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @Disabled("존재하지 않는 유저는 토큰 필터에서 걸러짐")
    @DisplayName("존재하지 않는 사용자가 내 포스트 목록을 확인할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenGetMyFeedList_thenReturnsException() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.my(fixture.getUserId(), mock(Pageable.class)));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @DisplayName("올바른 사용자가 포스트 목록을 확인하면 성공한다.")
    @Test
    void givenUser_whenGetFeedList_thenReturnsSuccess() {
        Pageable pageable = mock(Pageable.class);
        when(postRepository.findAll(pageable)).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.list(pageable));
    }

    @DisplayName("올바른 사용자가 내 포스트 목록을 확인하면 성공한다.")
    @Test
    void givenUser_whenGetMyFeedList_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Pageable pageable = mock(Pageable.class);
        when(postRepository.findAllByUserId(any(), eq(pageable))).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.my(fixture.getUserId(), pageable));
    }

    @DisplayName("올바른 사용자가 좋아요를 누를 시 성공한다.")
    @Test
    void givenUser_whenLikes_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(postRepository.findById(any())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        assertDoesNotThrow(() -> postService.like(fixture.getPostId(), fixture.getUserName()));
    }

    @DisplayName("이미 좋아요를 누른 경우, 예외를 반환한다.")
    @Test
    void givenAlreadyLikedUser_whenLikes_thenReturnsException() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        LikeEntity mockLikeEntity = mock(LikeEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(any())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(mockUserEntity));
        when(likeRepository.findByUserAndPost(eq(mockUserEntity), eq(mockPostEntity))).thenReturn(Optional.of(mockLikeEntity));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.like(fixture.getPostId(), fixture.getUserName()));
        assertEquals(ErrorCode.ALREADY_LIKED_POST, exception.getErrorCode());
    }

    @DisplayName("올바른 사용자가 댓글을 작성할 시 성공한다.")
    @Test
    void givenUser_whenCreateComment_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        PostEntity mockPostEntity = mock(PostEntity.class);
        when(postRepository.findById(any())).thenReturn(Optional.of(mockPostEntity));
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        when(commentRepository.save(any())).thenReturn(mock(CommentEntity.class));
        assertDoesNotThrow(() -> postService.comment(fixture.getPostId(), fixture.getUserName(), "comment"));
    }

    @DisplayName("존재하지않는 유저가 댓글을 작성할 시 예외를 반환한다.")
    @Test
    void givenNotExistsUser_whenCreateComment_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(any())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.comment(fixture.getPostId(), any(), "comment"));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("없는 글에 댓글을 작성할 시 예외를 반환한다.")
    @Test
    void givenNotExistsPost_whenCreateComment_thenReturnsSuccess() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        SnsApplicationException exception = assertThrows(SnsApplicationException.class, () -> postService.comment(any(), fixture.getUserName(), "comment"));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("올바른 사용자가 댓글을 확인하면 성공한다.")
    @Test
    void givenUser_whenGetCommentList_thenReturnsSuccess() {
        Pageable pageable = mock(Pageable.class);
        PostEntity mockPostEntity = mock(PostEntity.class);
        when(postRepository.findById(any())).thenReturn(Optional.of(mockPostEntity));
        when(commentRepository.findAllByPost(mockPostEntity, pageable)).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.getComments(any(), pageable));
    }

}
