package org.project.portfolio.article.entity

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.project.portfolio.common.BaseEntity
import org.project.portfolio.user.entity.User

/** 게시글 엔티티 */
@Entity
class Article(
    /** 게시글 번호 */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * 게시글 제목 (200자 이하)
     * <p>JPA에서 String 옵션은 기본적으로 255자까지만 저장 가능하다.</p>
     * <p>DB마다 한글과 영어의 바이트 수가 다르기 때문에, 2배 정도로 잡아주는 것이 안전하다.</p>
     */
    @Column(length = 400)
    var title: String,

    /** 게시글 내용 */
    @Column(length = 2000)
    var content: String,

    /** 게시글 작성자 */
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var author: User

) : BaseEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    override fun toString(): String {
        return "Article(id=$id, title='$title', content='$content', author=$author)"
    }

}
