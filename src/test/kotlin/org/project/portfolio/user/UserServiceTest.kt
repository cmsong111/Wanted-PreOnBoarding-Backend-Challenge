package org.project.portfolio.user

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.project.portfolio.common.BusinessException
import org.project.portfolio.user.entity.User
import java.util.*

/** UserService 단위 테스트 */
@DisplayName("UserService 단위 테스트")
@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    @DisplayName("사용자 조회 성공")
    fun getUserSuccess() {
        // given
        val email: String = "test@test.com"

        val user: User = User(
            email = email,
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )

        Mockito.`when`(userRepository.findById(email)).thenReturn(Optional.of(user))

        // when
        val result: User = userService.getUser(email)

        // then
        assertNotNull(result)
    }

    @Test
    @DisplayName("사용자 조회 실패 - 사용자 없음")
    fun getUserFailUserNotFound() {
        // given
        val email: String = "test@test.com"



        Mockito.`when`(userRepository.findById(email)).thenReturn(Optional.empty())

        // when & then
        assertThrows<BusinessException> {
            userService.getUser(email)
        }

    }
}
