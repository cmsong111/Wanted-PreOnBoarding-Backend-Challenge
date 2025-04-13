package org.project.portfolio.article.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.SoftDelete
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity(name = "article_comments")
@EntityListeners(AuditingEntityListener::class)
@SoftDelete(columnName = "is_deleted")
class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(columnDefinition = "TEXT")
    var content: String,
    var authorId: Long,
    var articleId: Long,
    @CreatedDate
    var createdAt: Instant = Instant.now(),
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) : AbstractAggregateRoot<Comment>() {
    companion object {
        /** 댓글 생성 메소드 */
        fun create(
            content: String,
            authorId: Long,
            articleId: Long,
        ): Comment {
            return Comment(
                content = content,
                authorId = authorId,
                articleId = articleId,
            )
        }
    }

    /**
     * 댓글 수정 메소드
     */
    fun update(content: String): Comment {
        this.content = content
        return this
    }
}
