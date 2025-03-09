package org.project.portfolio.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.project.portfolio.common.entity.BaseEntity
import org.project.portfolio.user.entity.User

@Entity
class Comment(
    /** 댓글 번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    /** 댓글 내용 */
    var content: String,
    /** 댓글 작성자 */
    @ManyToOne
    var author: User,
) : BaseEntity() {
    companion object {
        /** 댓글 생성 메소드 */
        fun create(
            content: String,
            author: User,
        ): Comment {
            return Comment(
                content = content,
                author = author,
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
