package org.project.portfolio.article.domain.validator

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class ArticleValidator(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val clock: Clock = Clock.systemUTC(),
) {
    /**
     * 게시글 작성자인지 확인
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
     * 게시글이 수정 가능한지 확인 (작성자 본인 + 작성 후 10일 미만)
     */
    fun isEditable(
        articleId: Long,
        email: String,
    ): Boolean {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        if (article.authorId != user.id) {
            return false
        }

        return article.isEditableAt(Instant.now(clock))
    }
}
