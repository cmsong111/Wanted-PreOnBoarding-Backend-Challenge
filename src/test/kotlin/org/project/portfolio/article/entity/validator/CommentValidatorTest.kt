package org.project.portfolio.article.entity.validator

import ArticleBuilder
import ArticleBuilder.createArticle
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleJpaRepository
import org.springframework.data.repository.findByIdOrNull

@DisplayName("댓글 도메인 검증기 테스트")
class CommentValidatorTest {
    private val articleRepository: ArticleJpaRepository = mockk()
    private val commentValidator = CommentValidator(articleRepository)

    @Test
    fun `댓글 작성자인지 확인 - 성공케이스`() {
        // given
        val articleId: Long = 1L
        val commentId: Long = 1L
        val userId: Long = 1L

        val article: Article = createArticle(
            id = articleId,
            comments = listOf(
                ArticleBuilder.CommentData(
                    id = commentId,
                    authorId = userId,
                ),
            ),
        )

        every { articleRepository.findByIdOrNull(articleId) } returns article

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

        val article: Article = createArticle(
            id = articleId,
            comments = listOf(
                ArticleBuilder.CommentData(
                    id = commentId,
                    authorId = 2L,
                ),
            ),
        )

        every { articleRepository.findByIdOrNull(articleId) } returns article

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
