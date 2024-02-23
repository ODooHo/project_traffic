package com.traffic_project.sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic_project.sns.dto.request.PostCommentRequest;
import com.traffic_project.sns.dto.request.PostModifyRequest;
import com.traffic_project.sns.dto.request.PostWriteRequest;
import com.traffic_project.sns.exception.ErrorCode;
import com.traffic_project.sns.exception.SnsApplicationException;
import com.traffic_project.sns.service.PostService;
import com.traffic_project.sns.util.ClassUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    ClassUtils classUtils;

    @Autowired
    private ObjectMapper objectMapper;


    @DisplayName("포스팅 요청이 오면 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingPosting_thenReturnsSuccess() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostWriteRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 사용자로 포스팅 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingPosting_thenReturnsException() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostWriteRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("로그인하지 않은 사용자로 포스팅 수정 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingModifyingPost_thenReturnsException() throws Exception {
        //when&then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostWriteRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("본인이 작성하지 않은 글로 포스팅 수정 요청이 오면 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotWriter_whenRequestingModifyingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        //when&then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostModifyRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @DisplayName("없는 글로 포스팅 수정 요청이 오면 예외를 반환한다.")
    @Test
    void givenNotExistingPost_whenRequestingModifyingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        //when&then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostModifyRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("포스팅 수정 요청 시 데이터베이스 에러 발생할 경우 예외를 반환한다.")
    @Test
    @WithMockUser
    void givenOccurDataBaseError_whenRequestingModifyingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        //when&then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostModifyRequest.of("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @DisplayName("로그인하지 않은 상태로 포스트 삭제 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingDeletingPost_thenReturnsException() throws Exception {
        //when&then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("본인이 작성하지 않은 글로 삭제 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotWriter_whenRequestingDeletingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), eq(1));
        //when&then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @DisplayName("없는 글로 포스트 삭제 요청 시 예외를 반환한다.")
    @Test
    void givenNotExistingPost_whenRequestingDeletingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), eq(1));
        //when&then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("삭제 요청 시 데이터베이스 에러가 발생할 경우 예외를 반환한다.")
    @Test
    @WithMockUser
    void givenOccurDataBaseError_whenRequestingDeletingPost_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).delete(any(), eq(1));
        //when&then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @DisplayName("피드목록 요청시 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingFeedList_thenReturnsSuccess() throws Exception {
        //given
        when(postService.list(any())).thenReturn(Page.empty());
        //when&then
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 사용자가 피드 목록 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingFeedList_thenReturnsException() throws Exception {
        //given
        when(postService.list(any())).thenReturn(Page.empty());
        //when&then
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("내 피드목록 요청시 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingMyFeedList_thenReturnsSuccess() throws Exception {
        //given
        when(postService.my(any(), any())).thenReturn(Page.empty());
        //when&then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 사용자가 내 피드 목록 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingMyFeedList_thenReturnsException() throws Exception {
        //given
        when(postService.my(any(), any())).thenReturn(Page.empty());
        //when&then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("좋아요 요청 시 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingLikes_thenReturnsSuccess() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인 하지 않은 사용자가 좋아요 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingLikes_thenReturnsException() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("없는 게시물에 좋아요 요청 시 예외를 반환한다.")
    @Test
    @WithMockUser
    void givenNotExistsPost_whenRequestingLikes_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("댓글 작성 요청 시 성공 응답을 반환한다.")
    @Test
    @WithMockUser
    void givenNothing_whenRequestingComments_thenReturnsSuccess() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostCommentRequest.of("comment"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 사용자가 댓글 작성 요청 시 예외를 반환한다.")
    @Test
    @WithAnonymousUser
    void givenNotAuthenticatedUser_whenRequestingComments_thenReturnsException() throws Exception {
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostCommentRequest.of("comment"))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("없는 게시글에 댓글 작성 요청 시 예외를 반환한다.")
    @Test
    @WithMockUser
    void givenNotExistsPost_whenRequestingComments_thenReturnsException() throws Exception {
        //given
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(), any(), any());
        //when&then
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PostCommentRequest.of("comment"))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
