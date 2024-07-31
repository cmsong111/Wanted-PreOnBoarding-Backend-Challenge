package org.project.portfolio.article.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.notification.service.NotificationService
import org.project.portfolio.user.entity.User
import java.sql.Timestamp

@DisplayName("ArticleScheduler 단위 테스트")
@ExtendWith(MockitoExtension::class)
class ArticleSchedulerTest {

    @InjectMocks
    private lateinit var articleScheduler: ArticleScheduler

    @Mock
    private lateinit var articleRepository: ArticleRepository

    @Mock
    private lateinit var notificationService: NotificationService

    @Test
    fun sendArticleUpdateNotification() {
        // given
        val user: User = User(
            email = "",
            password = "",
            name = "",
            phone = ""
        )
        val articles: List<Article> = listOf(
            Article(
                title = "제목",
                content = "내용",
                author = user
            ),
            Article(
                title = "제목",
                content = "내용",
                author = user
            )
        )
        Mockito.`when`(articleRepository.findByCreatedAtBetween(any<Timestamp>(), any<Timestamp>()))
            .thenReturn(articles)
        Mockito.doNothing().`when`(notificationService)
            .sendNotification(any(), any(), any(), any())

        // when
        articleScheduler.sendArticleUpdateNotification()
        // then
        Mockito.verify(articleRepository).findByCreatedAtBetween(any<Timestamp>(), any<Timestamp>())
    }
}
