import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.project.portfolio.auth.presentation.request.EmailCheckForm
import org.project.portfolio.auth.presentation.request.RegisterForm

object UsersFixture {
    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .plugin(SimpleValueJqwikPlugin())
        .plugin(JakartaValidationPlugin())
        .build()

    fun getRandomRegisterRequest(size: Int): List<RegisterForm> {
        return fixtureMonkey.giveMe(size)
    }

    fun getRandomRegisterRequest(): RegisterForm {
        return fixtureMonkey.giveMeOne()
    }

    fun getRandomEmailCheckRequest(): EmailCheckForm {
        return fixtureMonkey.giveMeOne()
    }
}
