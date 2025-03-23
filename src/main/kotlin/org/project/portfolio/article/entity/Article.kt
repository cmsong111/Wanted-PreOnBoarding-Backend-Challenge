package org.project.portfolio.article.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.OrderColumn
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.project.portfolio.common.entity.BaseEntity
import org.project.portfolio.user.entity.User
import java.time.Instant

/** 게시글 엔티티 */
@Entity
class Article(
    /** 게시글 번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    /**
     * 게시글 제목 (200자 이하)
     * <p>JPA에서 String 옵션은 기본적으로 255자까지만 저장 가능하다.</p>
     * <p>DB마다 한글과 영어의 바이트 수가 다르기 때문에, 2배 정도로 잡아주는 것이 안전하다.</p>
     */
    @Column(length = 400)
    var title: String,
    /** 게시글 내용 */
    @Column(columnDefinition = "LONGTEXT")
    var content: String,
    /** 게시글 이미지 */
    @ElementCollection
    @CollectionTable(name = "article_images")
    @OrderColumn(name = "image_order")
    var images: MutableList<String> = mutableListOf(),
    /** 게시글 댓글 */
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @OrderBy("createdAt DESC")
    var comments: MutableList<Comment> = mutableListOf(),
    /** 게시글 작성자 */
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var author: User?,
    /** 조회수 */
    var viewCount: Long = 0,
    override var createdAt: Instant = Instant.now(),
    override var updatedAt: Instant = Instant.now(),
) : BaseEntity() {
    companion object {
        fun create(
            title: String,
            content: String,
            images: List<String>? = null,
            author: User,
        ): Article {
            return Article(
                title = title,
                content = content,
                images = images?.toMutableList() ?: mutableListOf(),
                author = author,
            )
        }
    }

    fun update(
        title: String,
        content: String,
        images: List<String>? = null,
    ) {
        this.title = title
        this.content = content
        this.images = images?.toMutableList() ?: mutableListOf()
    }

    /**
     * 댓글 추가 메소드
     * @param content 댓글 내용
     * @param author 댓글 작성자
     * @return 추가된 댓글
     */
    fun addComment(
        content: String,
        author: User,
    ): Comment {
        val comment = Comment.create(
            content = content,
            author = author,
            article = this,
        )
        this.comments.add(
            comment,
        )
        return comment
    }

    /**
     * 댓글 수정 메소드
     * @param commentId 댓글 ID
     * @param content 댓글 내용
     * @return 수정된 댓글
     */
    fun updateComment(
        commentId: Long,
        content: String,
    ): Comment? {
        return this.comments.find {
            it.id == commentId
        }?.update(content)
    }

    /**
     * 댓글 삭제 메소드
     * @param commentId 댓글 ID
     * @return 삭제 여부
     */
    fun removeComment(commentId: Long): Boolean {
        return this.comments.removeIf {
            it.id == commentId
        }
    }
}
