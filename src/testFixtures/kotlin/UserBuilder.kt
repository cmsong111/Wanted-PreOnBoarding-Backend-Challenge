import org.project.portfolio.user.entity.User

object UserBuilder {
    fun createUser(
        id: Long = 1L,
        email: String = "test$userSuffix@test.com",
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

    private val userSuffix: String
        get() = (1..1000).random().toString()
}
