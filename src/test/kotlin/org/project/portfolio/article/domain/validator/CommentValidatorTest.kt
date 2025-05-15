package org.project.portfolio.article.domain.validator

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository

@DisplayName("댓글 도메인 검증기 테스트")
class CommentValidatorTest {
    private val commentRepository: CommentRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val commentValidator = CommentValidator(commentRepository, userRepository)

    @Test
    fun `댓글 작성자인지 확인 - 성공케이스`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )

        val comment = Comment(
            id = 1L,
            content = "댓글 내용입니다",
            authorId = user.id,
            articleId = 1L,
        )

        every { userRepository.findByEmail(user.email) } returns user
        every { commentRepository.findByArticleIdAndId(comment.articleId, comment.id) } returns comment

        // when
        val result = commentValidator.isAuthor(
            articleId = comment.articleId,
            commentId = comment.id,
            email = user.email,
        )

        // then
        assertTrue(result)
    }

    @Test
    fun `댓글 작성자인지 확인 - 실패케이스(본인 아님)`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )

        val comment = Comment(
            id = 1L,
            content = "댓글 내용입니다",
            authorId = 2L,
            articleId = 1L,
        )

        every { userRepository.findByEmail(user.email) } returns user
        every { commentRepository.findByArticleIdAndId(comment.articleId, comment.id) } returns comment

        // when
        val result = commentValidator.isAuthor(
            articleId = comment.articleId,
            commentId = comment.id,
            email = user.email,
        )

        // then
        assertFalse(result)
    }
}
