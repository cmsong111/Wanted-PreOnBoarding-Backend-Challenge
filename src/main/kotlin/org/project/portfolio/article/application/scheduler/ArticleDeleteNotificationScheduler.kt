package org.project.portfolio.article.application.scheduler

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.application.ArticleManageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ArticleDeleteNotificationScheduler(
    private val articleManageService: ArticleManageService,
) {
    /**
     * 매시 정각에 현재 시간기준 -9일, +1시간 이내의 게시글을 조회해서 내일까지 수정가능합니다 알림을 전송하는 스케줄러
     * @param start 게시글 조회 시작 시간 (현재 시간기준 -9일, +1시간)
     * @param end 게시글 조회 종료 시간 (현재 시간기준 -9일)
     */
    @Scheduled(cron = "0 0 * * * *")
    fun sendArticleUpdateNotification() {
        logger.info { "게시글 수정가능 알림 전송 스케줄러 시작" }
        articleManageService.sendArticleUpdateNotification()
        logger.info { "게시글 수정가능 알림 전송 스케줄러 종료" }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
