package org.project.portfolio.article.domain.validator

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class ArticleValidator(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 게시글 작성자인지 확인
     * @param articleId 게시글 ID
     * @param email 유저 이메일
     * @return 게시글 작성자 여부 (true: 작성자, false: 작성자 아님)
     */
    fun isAuthor(
        articleId: Long,
        email: String,
    ): Boolean {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        return article.authorId == user.id
    }

    /**
     * 게시글이 수정 가능한지 확인
     * <p>작성 후 10일이 지나면 수정 불가능</p>
     * @param articleId 게시글 ID
     * @param email 유저 이메일
     */
    fun isEditable(
        articleId: Long,
        email: String,
    ): Boolean {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        // 게시글 작성자와 현재 사용자가 다르면 수정 불가능
        if (article.authorId != user.id) {
            return false
        }

        // 게시글 작성 후 10일이 지나면 수정 불가능
        val diffDays: Long = article.createdAt.until(Instant.now(), ChronoUnit.DAYS)
        return diffDays < 10
    }
}
