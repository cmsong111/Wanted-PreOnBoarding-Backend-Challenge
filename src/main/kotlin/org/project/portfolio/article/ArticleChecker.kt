package org.project.portfolio.article

import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

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
    fun isEditable(id: Long): Boolean {
        // 게시글이 존재하지 않는 경우 수정 불가능
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 작성자가 아닌 경우 수정 불가능
        if (!isAuthor(id)) {
            return false
        }

        // 작성 후 10일이 지나지 않은 경우 수정 가능
        val now = Date()
        val diff: Long = now.time - article.createdAt.time
        val diffDays: Long = diff / (24 * 60 * 60 * 1000)
        return diffDays < 10
    }

    /**
     * 본인 게시글 여부 확인
     */
    fun isAuthor(id: Long): Boolean {
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
        val authentication: Authentication = SecurityContextHolder.getContext().authentication

        return authentication.name == article.author.email
    }
}
