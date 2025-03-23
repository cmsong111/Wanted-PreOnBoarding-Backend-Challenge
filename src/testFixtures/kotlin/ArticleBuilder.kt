import UserBuilder.createUser
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.entity.Comment
import java.time.Instant

object ArticleBuilder {
    /**
     * Article 객체를 생성하는 메소드
     * @param id 게시글 ID
     * @param title 게시글 제목
     * @param content 게시글 내용
     * @param authorId 게시글 작성자 ID
     * @param createdAt 게시글 생성일자
     * @param updatedAt 게시글 수정일자
     * @param comments 댓글 목록
     */
    fun createArticle(
        id: Long = 1L,
        title: String = "title",
        content: String = "content",
        authorId: Long = 1L,
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = createdAt,
        comments: List<CommentData> = emptyList(),
    ): Article {
        val article = Article(
            id = id,
            title = title,
            content = content,
            author = createUser(id = authorId),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        article.comments.addAll(
            comments.map {
                createComment(
                    id = it.id,
                    content = it.content,
                    authorId = it.authorId,
                    article = article,
                )
            },
        )

        return article
    }

    /**
     * Comment 객체를 생성하는 메소드
     * @param id 댓글 ID
     * @param content 댓글 내용
     * @param authorId 작성자 ID
     * @param createdAt 생성일자
     * @param updatedAt 수정일자
     * @param article 게시글 객체
     */
    fun createComment(
        id: Long = 1L,
        content: String = "content",
        authorId: Long = 1L,
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = createdAt,
        article: Article,
    ): Comment {
        return Comment(
            id = id,
            content = content,
            author = createUser(id = authorId),
            createdAt = createdAt,
            updatedAt = updatedAt,
            article = article,
        )
    }

    /**
     * 댓글 데이터 클래스
     */
    data class CommentData(
        val id: Long = 1L,
        val content: String = "content",
        val authorId: Long = 1L,
    )
}
