package org.project.portfolio.article.application.scheduler

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.application.ArticleCommandService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ArticleDeleteNotificationScheduler(
    private val articleCommandService: ArticleCommandService,
) {
    /**
     * 매시 정각에 현재 시간기준 -9일, +1시간 이내의 게시글을 조회해서 내일까지 수정가능합니다 알림을 전송하는 스케줄러
     */
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    fun sendArticleUpdateNotification() {
        logger.info { "게시글 수정가능 알림 전송 스케줄러 시작" }
        articleCommandService.sendArticleUpdateNotification()
        logger.info { "게시글 수정가능 알림 전송 스케줄러 종료" }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
