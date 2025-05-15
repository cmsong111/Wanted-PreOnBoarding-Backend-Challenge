import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.presentation.request.ArticleForm
import java.time.Instant

object ArticleFixture {
    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .plugin(SimpleValueJqwikPlugin())
        .plugin(JakartaValidationPlugin())
        .build()

    fun getRandomArticle(authorId: Long): ArticleForm {
        return fixtureMonkey.giveMeBuilder<ArticleForm>()
            .set("images", null)
            .set("authorId", authorId)
            .sample()
    }

    fun getRandomArticle(size: Int): List<ArticleForm> {
        return fixtureMonkey.giveMeBuilder<ArticleForm>()
            .set("images", null)
            .sampleList(size)
    }

    fun createArticle(
        articleForm: ArticleForm? = getRandomArticle(1)[0],
        authorId: Long = 1L,
        articleId: Long = 1L,
        createdAt: Instant = Instant.now(),
    ): Article {
        return Article(
            id = articleId,
            title = articleForm?.title ?: "제목입니다",
            content = articleForm?.content ?: "내용입니다",
            images = listOf<String>(),
            authorId = authorId,
            createdAt = createdAt,
        )
    }
}
