package org.project.portfolio.article.service

import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.notification.service.NotificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class ArticleScheduler(
    private val articleRepository: ArticleRepository,
    private val notificationService: NotificationService,
) {
    private val logger: Logger = LoggerFactory.getLogger(ArticleScheduler::class.java)

    /**
     * 매시 정각에 현재 시간기준 -9일, +1시간 이내의 게시글을 조회해서 내일까지 수정가능합니다 알림을 전송하는 스케줄러
     */
    @Scheduled(cron = "0 0 * * * *")
    fun sendArticleUpdateNotification() {
        logger.info("게시글 수정가능 알림 전송 스케줄러 시작")
        val start = Timestamp(System.currentTimeMillis() - 9 * 24 * 60 * 60 * 1000)
        val end = Timestamp(System.currentTimeMillis() - 9 * 24 * 60 * 60 * 1000 + 1 * 60 * 60 * 1000)
        logger.info("$start ~ $end 사이의 게시글 조회")
        // 게시글 조회
        val articles: List<Article> = articleRepository.findByCreatedAtBetween(start, end)
        logger.info("조회된 게시글 수: ${articles.size}")
        articles.forEach {
            notificationService.sendNotification(
                it.author.email,
                "게시글 수정가능 알림",
                "게시글 '${it.title}'이 내일까지 수정가능합니다",
                "system"
            )
        }
        logger.info("게시글 수정가능 알림 전송 스케줄러 종료")
    }
}
