package org.project.portfolio.comment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import org.project.portfolio.auth.JwtAuthFilter
import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.comment.dto.CommentRequest
import org.project.portfolio.comment.dto.CommentResponse
import org.project.portfolio.comment.service.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime


@WebMvcTest(CommentController::class)
class CommentControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var ctx: WebApplicationContext

    @MockBean
    private lateinit var jwtAuthFilter: JwtAuthFilter

    @MockBean
    private lateinit var jwtProvider: JwtProvider

    @MockBean
    private lateinit var commentService: CommentService


    /**
     * 테스트 실행 전 MockMvc 객체 초기화 (Jwt를 적용했기 때문에 MockMvc 객체를 생성할 때 springSecurity()를 적용해야 함)
     */
    @BeforeEach
    fun setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(ctx)
            .apply<DefaultMockMvcBuilder?>(springSecurity()).build()
    }

    @Test
    @DisplayName("댓글 생성 API 테스트 - 성공")
    @WithMockUser(username = "test@test.com", roles = ["USER", "ADMIN"])
    fun createComment() {
        val articleId: Long = 1

        val commentRequest: CommentRequest = CommentRequest(
            content = "content"
        )

        val expectedCommentResponse = CommentResponse(
            id = 1L,
            content = commentRequest.content!!,
            articleId = articleId,
            userId = "test@test.com",
            userName = "user",
            createdAt = LocalDateTime.now().toString(),
            updatedAt = LocalDateTime.now().toString()
        )

        whenever(
            commentService.createComment(
                anyString(),
                anyLong(),
                any<CommentRequest>()
            )
        ).thenReturn(expectedCommentResponse)


        // when & then
        mvc.perform(
            post("/api/v1/article/$articleId/comment")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(commentRequest))
        )
            .andExpect(status().isCreated) // 201 Created

        verify(commentService, atLeastOnce()).createComment(anyString(), anyLong(), any<CommentRequest>())
    }

    @Test
    @DisplayName("댓글 생성 API 테스트 - 실패(유저 없음)")
    @WithAnonymousUser
    fun createCommentFailNoUser() {
        val articleId: Long = 1

        val commentRequest = CommentRequest(
            content = "content"
        )

        // when & then
        mvc.perform(
            post("/api/v1/article/$articleId/comment")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(commentRequest))
        )
            .andExpect(status().isUnauthorized) // 401 Unauthorized

        verify(commentService, times(0)).createComment(anyString(), anyLong(), any<CommentRequest>())
    }

    @Test
    @DisplayName("댓글 생성 API 테스트 - 실패(게시글 없음)")
    @WithMockUser(username = "test@test.com", roles = ["USER", "ADMIN"])
    fun updateComment() {
        // given
        val articleId: Long = 1
        val commentId: Long = 1

        val commentRequest: CommentRequest = CommentRequest(
            content = "content"
        )

        val expectedCommentResponse = CommentResponse(
            id = 1L,
            content = commentRequest.content!!,
            articleId = articleId,
            userId = "test@test.com",
            userName = "user",
            createdAt = LocalDateTime.now().toString(),
            updatedAt = LocalDateTime.now().toString()
        )

        given(
            commentService.updateComment(
                anyLong(),
                any<CommentRequest>()
            )
        ).willReturn(
            expectedCommentResponse
        )

        // when
        mvc.perform(
            patch("/api/v1/article/$articleId/comment/$commentId")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(commentRequest))
        )
            .andExpect(status().isOk) // 200 OK

    }


    @Test
    @DisplayName("댓글 삭제 API 테스트 - 성공")
    @WithMockUser(username = "test@test.com", roles = ["USER", "ADMIN"])
    fun deleteComment() {
        // given
        val articleId: Long = 1
        val commentId: Long = 1

        // when & then
        mvc.perform(
            delete("/api/v1/article/$articleId/comment/$commentId")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent) // 204 No Content

        verify(commentService, atLeastOnce()).deleteComment(anyLong())
    }


}
