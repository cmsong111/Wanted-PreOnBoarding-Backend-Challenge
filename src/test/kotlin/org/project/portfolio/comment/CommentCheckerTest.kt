package org.project.portfolio.comment

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.project.portfolio.article.entity.Article
import org.project.portfolio.comment.entity.Comment
import org.project.portfolio.comment.repository.CommentRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.user.entity.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*


@DisplayName("CommentChecker 단위 테스트")
@ExtendWith(MockitoExtension::class)
class CommentCheckerTest {

    @InjectMocks
    private lateinit var commentChecker: CommentChecker

    @Mock
    private lateinit var commentRepository: CommentRepository

    @Test
    fun isAuthor() {

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

        val expectComment: Comment = Comment(
            id = 1L,
            content = "내용",
            author = author,
            article = article
        )

        // when
        val context: SecurityContext = SecurityContextHolder.getContext()
        context.authentication = UsernamePasswordAuthenticationToken(author.email, author.password, author.authorities)

        Mockito.`when`(commentRepository.findById(anyLong())).thenReturn(Optional.of(expectComment))

        // then
        assert(commentChecker.isAuthor(1L))

    }

    @Test
    @DisplayName("댓글 작성자 확인 - 실패")
    fun isAuthorFail() {

        // given
        val author: User = User(
            email = "user@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )


        // when
        val context: SecurityContext = SecurityContextHolder.getContext()
        context.authentication = UsernamePasswordAuthenticationToken(author.email, author.password, author.authorities)

        Mockito.`when`(commentRepository.findById(anyLong())).thenReturn(Optional.empty())

        // then
        assertThrows<BusinessException> {
            commentChecker.isAuthor(1L)
        }
    }
}
