package org.project.portfolio.article.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.time.Instant

@RedisHash(value = "article_views", timeToLive = 60 * 60 * 24)
data class ArticleView(
    @Id
    val id: String,
    val articleId: Long,
    val ip: String,
    val userAgent: String,
    val createdAt: Instant,
) : Serializable {
    companion object {
        /**
         * ArticleView 객체를 생성하는 메소드
         * ID의 경우 Redis에서 매우 중요하게 사용되는 값이므로, createKey 메소드를 통해 생성
         * @param articleId 게시글 ID
         * @param ip 접속 IP
         * @param userAgent 접속 UserAgent
         */
        fun create(
            articleId: Long,
            ip: String,
            userAgent: String,
        ): ArticleView {
            return ArticleView(
                id = createKey(articleId, ip, userAgent),
                articleId = articleId,
                ip = ip,
                userAgent = userAgent,
                createdAt = Instant.now(),
            )
        }

        /**
         * ArticleView 객체의 ID를 생성하는 메소드
         * @param articleId 게시글 ID
         * @param ip 접속 IP
         * @param userAgent 접속 UserAgent
         */
        fun createKey(
            articleId: Long,
            ip: String,
            userAgent: String,
        ): String {
            return "article_views:$articleId:$ip:$userAgent"
        }
    }
}
