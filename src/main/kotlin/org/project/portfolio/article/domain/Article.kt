package org.project.portfolio.article.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OrderColumn
import org.hibernate.annotations.SoftDelete
import org.project.portfolio.common.domain.BaseTimeEntity
import java.time.Instant
import java.time.temporal.ChronoUnit

/** 게시글 엔티티 */
@Entity
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
    /** 게시글 태그 */
    @ElementCollection
    @CollectionTable(name = "article_tags")
    var tags: MutableSet<String> = mutableSetOf(),
    /** 게시글 작성자 */
    var authorId: Long,
    /** 조회수 */
    var viewCount: Long = 0,
) : BaseTimeEntity() {
    companion object {
        const val EDITABLE_DAYS: Long = 10L

        fun create(
            title: String,
            content: String,
            images: List<String> = emptyList(),
            tags: List<String> = emptyList(),
            authorId: Long,
        ): Article {
            return Article(
                title = title,
                content = content,
                images = images.toMutableList(),
                tags = tags.map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet(),
                authorId = authorId,
            )
        }
    }

    /** 작성일 기준 수정 가능 여부 확인 (10일 미만) */
    fun isEditableAt(now: Instant): Boolean {
        return createdAt.until(now, ChronoUnit.DAYS) < EDITABLE_DAYS
    }

    /**
     * 게시글 수정 메서드
     */
    fun update(
        title: String?,
        content: String?,
        images: List<String>? = null,
        tags: List<String>? = null,
    ) {
        title?.let { this.title = it }
        content?.let { this.content = it }
        images?.let { this.images = it }
        tags?.let {
            this.tags.clear()
            this.tags.addAll(it.map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() })
        }
    }
}
