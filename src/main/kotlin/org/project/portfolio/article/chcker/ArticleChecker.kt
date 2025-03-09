package org.project.portfolio.article.chcker

import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class ArticleChecker(
    private val articleRepository: ArticleRepository,
) {
    /**
     * 게시글 수정 가능 여부 확인
     *
     * @param id 게시글 ID
     * @return 수정 가능 여부 (true: 수정 가능, false: 수정 불가능)
     */
    fun isEditable(
        id: Long,
        now: Instant = Instant.now(),
    ): Boolean {
        // 게시글이 존재하지 않는 경우 수정 불가능
        val article: Article = articleRepository.findByIdOrNull(id)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 작성자가 아닌 경우 수정 불가능
        if (!isAuthor(id)) {
            return false
        }

        // 작성 후 10일이 지나지 않은 경우 수정 가능
        val diffDays = ChronoUnit.DAYS.between(
            article.createdAt,
            now,
        )
        return diffDays < 10
    }

    /**
     * 본인 게시글 여부 확인
     */
    fun isAuthor(id: Long): Boolean {
        val article: Article = articleRepository.findByIdOrNull(id)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        val authentication: Authentication = SecurityContextHolder.getContext().authentication

        return authentication.name == article.author!!.email
    }
}
