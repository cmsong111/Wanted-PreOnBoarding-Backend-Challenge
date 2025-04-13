package org.project.portfolio.article.domain.validator

import CommentFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository

@DisplayName("댓글 도메인 검증기 테스트")
class CommentValidatorTest {
    private val commentRepository: CommentRepository = mockk()
    private val commentValidator = CommentValidator(commentRepository)

    @Test
    fun `댓글 작성자인지 확인 - 성공케이스`() {
        // given
        val articleId: Long = 1L
        val commentId: Long = 1L
        val userId: Long = 1L

        val comment: Comment = CommentFixture.createComment(
            id = commentId,
            authorId = userId,
            articleId = articleId,
        )

        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns comment

        // when
        val result = commentValidator.isAuthor(articleId, commentId, userId)

        // then
        assertTrue(result)
    }

    @Test
    fun `댓글 작성자인지 확인 - 실패케이스(본인 아님)`() {
        // given
        val articleId: Long = 1L
        val commentId: Long = 1L
        val userId: Long = 1L

        // 다른 작성자 ID (2L)로 댓글 생성
        val comment: Comment = CommentFixture.createComment(
            id = commentId,
            authorId = 2L,
            articleId = articleId,
        )

        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns comment

        // when
        val result = commentValidator.isAuthor(
            articleId = articleId,
            commentId = commentId,
            userId = userId,
        )

        // then
        assertFalse(result)
    }
}
