package org.project.portfolio.article.domain.validator

import ArticleBuilder.createArticle
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.exception.ArticleNotFoundException
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant

@DisplayName("게시글 도메인 검증기 테스트")
class ArticleValidatorTest {
    private val articleRepository: ArticleRepository = mockk()
    private val articleValidator = ArticleValidator(articleRepository)

    @Test
    fun `게시글 작성자 확인 - 성공 케이스`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L
        val article: Article = createArticle(
            articleId = articleId,
            authorId = userId,
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article

        // when
        val result = articleValidator.isAuthor(articleId, userId)

        // then
        assertTrue(result)
    }

    @Test
    fun `게시글 작성자인지 확인 - 실패 케이스(본인 아님)`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L
        // 게시글 작성자 ID가 2인 경우
        val article: Article = createArticle(
            articleId = articleId,
            authorId = 2L,
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article

        // when
        val result = articleValidator.isAuthor(articleId, userId)

        // then
        assertFalse(result)
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 성공 케이스`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L
        val article: Article = createArticle(
            articleId = articleId,
            authorId = userId,
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article

        // when
        val result = articleValidator.isEditable(articleId, userId)

        // then
        assertTrue(result)
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 실패 케이스(작성자 아님)`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L
        val article: Article = createArticle(
            articleId = articleId,
            authorId = 2L,
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article

        // when
        val result = articleValidator.isEditable(articleId, userId)

        // then
        assertFalse(result)
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 실패 케이스(10일이 지남)`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L
        // 11일 전 생성된 게시글 생성
        val article: Article = createArticle(
            articleId = articleId,
            authorId = userId,
            createdAt = Instant.now().minusSeconds(11 * 24 * 60 * 60),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article

        // when
        val result = articleValidator.isEditable(articleId, userId)

        // then
        assertFalse(result)
    }

    @Test
    fun `게시글이 존재하지 않은 경우 - 실패 케이스`() {
        // given
        val articleId: Long = 1L
        val userId: Long = 1L

        every { articleRepository.findByIdOrNull(articleId) } returns null

        // when & then
        assertThrows<ArticleNotFoundException> {
            articleValidator.isEditable(articleId, userId)
        }
    }
}
