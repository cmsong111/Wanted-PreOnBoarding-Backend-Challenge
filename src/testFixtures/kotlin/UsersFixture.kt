import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.project.portfolio.auth.presentation.request.RegisterRequest
import org.project.portfolio.user.domain.User

object UsersFixture {
    fun createUser(
        id: Long = 1L,
        email: String = "test$id@test.com",
        password: String = "Password1234!",
        name: String = "홍길동",
        phone: String = "010-1234-5678",
    ): User {
        return User(
            id = id,
            email = email,
            password = password,
            name = name,
            phone = phone,
        )
    }

    private val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .plugin(SimpleValueJqwikPlugin())
        .plugin(JakartaValidationPlugin())
        .build()

    fun getRandomRegisterRequest(size: Int): List<RegisterRequest> {
        return fixtureMonkey.giveMe(size)
    }

    fun getRandomRegisterRequest(): RegisterRequest {
        return fixtureMonkey.giveMeOne()
    }
}
