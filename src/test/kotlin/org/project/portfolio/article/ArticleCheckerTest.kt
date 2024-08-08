package org.project.portfolio.article

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.sql.Timestamp
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("ArticleChecker 단위 테스트")
@ExtendWith(MockitoExtension::class)
class ArticleCheckerTest {

    @InjectMocks
    private lateinit var articleChecker: ArticleChecker

    @Mock
    private lateinit var articleRepository: ArticleRepository

    @Mock
    private lateinit var userRepository: UserRepository


    @Test
    @DisplayName("게시글 수정 가능 - 작성자 본인")
    fun isEditable2() {
        // given
        val author: User = User(
            email = "user@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )
        val article: Article = Article(
            title = "제목",
            content = "내용",
            author = author
        )
        val context: SecurityContext = SecurityContextHolder.getContext()
        context.authentication = UsernamePasswordAuthenticationToken(author.email, author.password, author.authorities)

        Mockito.`when`(articleRepository.findById(1L)).thenReturn(Optional.of(article))

        assertTrue { articleChecker.isEditable(1L) }
    }

    @Test
    @DisplayName("게시글 수정 불가능 - 타 사용자")
    fun isEditable3() {
        // given
        val author: User = User(
            email = "user@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )
        val author2: User = User(
            email = "anotherUser@test.com",
            password = "Password1234~!",
            name = "김남주",
            phone = "010-1234-5678"
        )
        val article: Article = Article(
            title = "제목",
            content = "내용",
            author = author2
        )
        val context: SecurityContext = SecurityContextHolder.getContext()
        context.authentication = UsernamePasswordAuthenticationToken(author.email, author.password, author.authorities)

        Mockito.`when`(articleRepository.findById(1L)).thenReturn(Optional.of(article))

        assertFalse { articleChecker.isEditable(1L) }
    }

    @Test
    @DisplayName("게시글 수정 불가능 - 작성 후 10일이 지남")
    fun isEditable4() {
        // given
        val author: User = User(
            email = "user@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )
        val article: Article = Article(
            title = "제목",
            content = "내용",
            author = author
        )

        article.createdAt = Timestamp(System.currentTimeMillis() - 11 * 24 * 60 * 60 * 1000)
        val context: SecurityContext = SecurityContextHolder.getContext()
        context.authentication = UsernamePasswordAuthenticationToken(author.email, author.password, author.authorities)

        Mockito.`when`(articleRepository.findById(1L)).thenReturn(Optional.of(article))

        assertFalse { articleChecker.isEditable(1L) }
    }

    @Test
    @DisplayName("게시글 수정 불가능 - 게시글 없음")
    fun isEditable5() {
        // given
        val id: Long = 50

        // when
        Mockito.`when`(articleRepository.findById(id)).thenReturn(Optional.empty())

        // then
        assertThrows<BusinessException> {
            articleChecker.isEditable(id)
        }
    }
}
