package org.project.portfolio.comment.entity

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.project.portfolio.article.entity.Article
import org.project.portfolio.common.BaseEntity
import org.project.portfolio.user.entity.User

@Entity
class Comment(
    /** 댓글 번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /** 댓글 내용 */
    var content: String,

    /** 게시글 */
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    var article: Article,

    /** 댓글 작성자 */
    @ManyToOne
    var author: User,

    ) : BaseEntity() {

    fun update(content: String) {
        this.content = content
    }
}
