package org.project.portfolio.article.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OrderColumn
import org.hibernate.annotations.SoftDelete
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/** 게시글 엔티티 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@SoftDelete(columnName = "is_deleted")
class Article(
    /** 게시글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    /**
     * 게시글 제목 (200자 이하)
     * <p>JPA에서 String 옵션은 기본적으로 255자까지만 저장 가능하다.</p>
     * <p>DB마다 한글과 영어의 바이트 수가 다르기 때문에, 2배 정도로 잡아주는 것이 안전하다.</p>
     */
    @Column(length = 400)
    var title: String,
    /** 게시글 내용 */
    @Column(columnDefinition = "TEXT")
    var content: String,
    /** 게시글 이미지 */
    @ElementCollection
    @CollectionTable(name = "article_images")
    @OrderColumn(name = "image_order")
    var images: List<String> = mutableListOf(),
    /** 게시글 작성자 */
    var authorId: Long,
    /** 조회수 */
    var viewCount: Long = 0,
    /** 생성일 */
    @CreatedDate
    var createdAt: Instant = Instant.now(),
    /** 수정일 */
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) : AbstractAggregateRoot<Article>() {
    companion object {
        fun create(
            title: String,
            content: String,
            images: List<String> = emptyList(),
            authorId: Long,
        ): Article {
            return Article(
                title = title,
                content = content,
                images = images.toMutableList(),
                authorId = authorId,
            )
        }
    }

    /**
     * 게시글 수정 메서드
     * @param title 수정할 제목 (null이면 수정하지 않음)
     * @param content 수정할 내용 (null이면 수정하지 않음)
     * @param images 수정할 이미지 리스트 (null이면 수정하지 않음, 빈 리스트이면 이미지 삭제)
     */
    fun update(
        title: String?,
        content: String?,
        images: List<String>? = null,
    ) {
        title?.let { this.title = it }
        content?.let { this.content = it }
        images?.let { this.images = it }
    }
}
