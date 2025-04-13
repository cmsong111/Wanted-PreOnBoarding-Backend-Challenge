import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.presentation.request.CommentForm
import java.time.Instant

object CommentFixture {
    fun createComment(
        id: Long = 1L,
        content: String = "댓글 내용입니다",
        authorId: Long = 1L,
        articleId: Long = 1L,
        createdAt: Instant = Instant.now(),
    ): Comment {
        return Comment(
            id = id,
            content = content,
            authorId = authorId,
            articleId = articleId,
            createdAt = createdAt,
        )
    }

    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .plugin(SimpleValueJqwikPlugin())
        .plugin(JakartaValidationPlugin())
        .build()

    fun getRandomComment(size: Int): List<CommentForm> {
        return fixtureMonkey.giveMe(size)
    }

    fun getRandomComment(): CommentForm {
        return fixtureMonkey.giveMeOne()
    }
}
