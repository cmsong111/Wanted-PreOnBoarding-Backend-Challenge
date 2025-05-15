package org.project.portfolio.article.domain.validator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.time.temporal.ChronoUnit

@DisplayName("게시글 도메인 검증기 테스트")
class ArticleValidatorTest : AnnotationSpec() {
    private val articleRepository: ArticleRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val articleValidator = ArticleValidator(articleRepository, userRepository)

    @Test
    fun `게시글 작성자 확인 - 성공 케이스`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )
        val article = Article(
            id = 1L,
            title = "Test Title",
            content = "Test Content",
            authorId = user.id,
            createdAt = Instant.now(),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article
        every { userRepository.findByEmail(user.email) } returns user

        // when
        val result = articleValidator.isAuthor(article.id, user.email)

        // then
        result shouldBe true
    }

    @Test
    fun `게시글 작성자인지 확인 - 실패 케이스(본인 아님)`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )
        // 다른 작성자
        val article = Article(
            id = 1L,
            title = "Test Title",
            content = "Test Content",
            authorId = 2L,
            createdAt = Instant.now(),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article
        every { userRepository.findByEmail(user.email) } returns user

        // when
        val result = articleValidator.isAuthor(article.id, user.email)

        // then
        result shouldBe false
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 성공 케이스`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )
        val article = Article(
            id = 1L,
            title = "Test Title",
            content = "Test Content",
            authorId = user.id,
            createdAt = Instant.now(),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article
        every { userRepository.findByEmail(user.email) } returns user

        // when
        val result = articleValidator.isEditable(article.id, user.email)

        // then
        result shouldBe true
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 실패 케이스(작성자 아님)`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )
        // 다른 작성자
        val article = Article(
            id = 1L,
            title = "Test Title",
            content = "Test Content",
            authorId = 2L,
            createdAt = Instant.now(),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article
        every { userRepository.findByEmail(user.email) } returns user

        // when
        val result = articleValidator.isEditable(article.id, user.email)

        // then
        result shouldBe false
    }

    @Test
    fun `게시글 수정 가능한지 확인 - 실패 케이스(10일이 지남)`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )
        // 10일 전 작성된 게시글
        val article = Article(
            id = 1L,
            title = "Test Title",
            content = "Test Content",
            authorId = user.id,
            createdAt = Instant.now().minus(10, ChronoUnit.DAYS),
        )

        every { articleRepository.findByIdOrNull(article.id) } returns article
        every { userRepository.findByEmail(user.email) } returns user

        // when
        val result = articleValidator.isEditable(article.id, user.email)

        // then
        result shouldBe false
    }

    @Test
    fun `게시글이 존재하지 않은 경우 - 실패 케이스`() {
        // given
        val user = User(
            id = 1L,
            name = "TestUser",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
        )

        every { articleRepository.findByIdOrNull(any()) } returns null
        every { userRepository.findByEmail(user.email) } returns user

        // when & then
        val exception = shouldThrow<NotFoundException> {
            articleValidator.isEditable(1L, user.email)
        }

        exception.message shouldContain Article::class.java.simpleName
    }
}
