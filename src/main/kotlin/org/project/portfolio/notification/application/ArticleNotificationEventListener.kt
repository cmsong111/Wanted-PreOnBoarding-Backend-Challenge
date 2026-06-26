package org.project.portfolio.notification.application

import org.project.portfolio.article.domain.event.ArticleEditDeadlineApproachingEvent
import org.project.portfolio.article.domain.event.CommentCreateEvent
import org.project.portfolio.notification.presentation.request.NotificationRequestDto
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ArticleNotificationEventListener(
    private val notificationCommandService: NotificationCommandService,
) {
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleCommentCreated(event: CommentCreateEvent) {
        notificationCommandService.sendNotification(
            notificationRequestDto = NotificationRequestDto(
                title = "새 댓글 알림",
                content = "'${event.articleTitle}' 게시글에 ${event.commentAuthorName}님이 댓글을 남겼습니다.",
                receiver = event.articleAuthorId,
                sender = "system",
            ),
            sender = "system",
        )
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleArticleEditDeadlineApproaching(event: ArticleEditDeadlineApproachingEvent) {
        notificationCommandService.sendNotification(
            notificationRequestDto = NotificationRequestDto(
                title = "게시글 수정 마감 알림",
                content = "게시글 '${event.articleTitle}'은(는) 내일까지 수정 가능합니다.",
                receiver = event.authorId,
                sender = "system",
            ),
            sender = "system",
        )
    }
}
