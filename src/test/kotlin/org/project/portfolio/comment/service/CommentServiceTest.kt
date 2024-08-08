package org.project.portfolio.comment.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.comment.dto.CommentRequest
import org.project.portfolio.comment.dto.CommentResponse
import org.project.portfolio.comment.entity.Comment
import org.project.portfolio.comment.repository.CommentRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@DisplayName("CommentService 단위 테스트")
@ExtendWith(MockitoExtension::class)
class CommentServiceTest {

    @InjectMocks
    private lateinit var commentService: CommentService

    @Mock
    private lateinit var commentRepository: CommentRepository

    @Mock
    private lateinit var articleRepository: ArticleRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    @DisplayName("댓글 생성 - 성공")
    fun createComment() {
        // given
        val name: String = "test@test.com"
        val articleId: Long = 1
        val commentRequest: CommentRequest = CommentRequest(content = "댓글 내용")
        val expectedUser = User(
            email = "test@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )
        val expectArticle = Article(
            id = 1,
            title = "제목",
            content = "내용",
            author = expectedUser
        )
        val expectComment = Comment(
            id = 1,
            article = expectArticle,
            author = expectedUser,
            content = commentRequest.content!!
        )

        // when
        Mockito.`when`(userRepository.findById(name)).thenReturn(Optional.of(expectedUser))
        Mockito.`when`(articleRepository.findById(articleId)).thenReturn(Optional.of(expectArticle))
        Mockito.`when`(commentRepository.save(any<Comment>())).thenReturn(expectComment)

        // then
        val result: CommentResponse = commentService.createComment(name, articleId, commentRequest)

        // expected
        assertNotEquals(result.id, 0)
        assertEquals(result.content, commentRequest.content)
        assertEquals(result.articleId, articleId)
    }

    @Test
    @DisplayName("댓글 생성 - 실패(게시글 없음)")
    fun createCommentFailNoArticle() {
        // given
        val name: String = "test@test.com"
        val articleId: Long = 1
        val commentRequest: CommentRequest = CommentRequest(content = "댓글 내용")


        // when
        Mockito.`when`(articleRepository.findById(anyLong())).thenReturn(Optional.empty())

        // then
        assertThrows<BusinessException> {
            commentService.createComment(name, articleId, commentRequest)
        }
    }

    @Test
    @DisplayName("댓글 생성 - 실패(유저 없음)")
    fun createCommentFailNoUser() {
        // given
        val name: String = "test@test.com"
        val articleId: Long = 1
        val commentRequest: CommentRequest = CommentRequest(content = "댓글 내용")

        val expectArticle = Article(
            id = 1,
            title = "제목",
            content = "내용",
            author = User(
                email = "test@test.com",
                password = "Password1234~!",
                name = "홍길동",
                phone = "010-1234-5678"
            )
        )

        // when
        Mockito.`when`(userRepository.findById(name)).thenReturn(Optional.empty())
        Mockito.`when`(articleRepository.findById(articleId)).thenReturn(Optional.of(expectArticle))

        // then
        assertThrows<BusinessException> {
            commentService.createComment(name, articleId, commentRequest)
        }
    }


    @Test
    @DisplayName("댓글 수정 - 성공")
    fun updateComment() {
        // given
        val id: Long = 1
        val commentRequest: CommentRequest = CommentRequest(content = "댓글 내용")
        val user = User(
            email = "test@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )
        val expectComment = Comment(
            id = 1,
            article = Article(
                id = 1,
                title = "제목",
                content = "내용",
                author = user
            ),
            author = user,
            content = "댓글 내용"
        )

        // when
        Mockito.`when`(commentRepository.findById(id)).thenReturn(Optional.of(expectComment))
        Mockito.`when`(commentRepository.save(any<Comment>())).thenReturn(expectComment)

        // then
        val result: CommentResponse = commentService.updateComment(id, commentRequest)

        assertEquals(result.id, id)
        assertEquals(result.content, commentRequest.content)
    }

    @Test
    @DisplayName("댓글 수정 - 실패(댓글 없음)")
    fun updateCommentFailNoComment() {
        // given
        val id: Long = 1
        val commentRequest: CommentRequest = CommentRequest(content = "댓글 내용")

        // when
        Mockito.`when`(commentRepository.findById(anyLong())).thenReturn(Optional.empty())

        // then
        assertThrows<BusinessException> {
            commentService.updateComment(id, commentRequest)
        }
    }

    @Test
    fun deleteComment() {
        // given
        Mockito.`when`(commentRepository.deleteById(anyLong())).then { }
        // when
        commentService.deleteComment(1)
        // then
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(anyLong())
    }
}
