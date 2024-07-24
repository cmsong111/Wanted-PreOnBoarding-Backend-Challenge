package org.project.portfolio.auth


import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.project.portfolio.auth.dto.LoginRequest
import org.project.portfolio.auth.dto.RegisterRequest
import org.project.portfolio.auth.dto.TokenResponse
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertNotNull

/** AuthService 단위 테스트 */
@DisplayName("AuthService 단위 테스트")
@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @InjectMocks
    private lateinit var authService: AuthService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var jwtProvider: JwtProvider

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder


    @Test
    @DisplayName("사용자 조회 성공")
    fun loadUserByUsername() {
        // given
        val username: String = "test@test.com";

        val user: User = User(
            email = username,
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678"
        )

        Mockito.`when`(userRepository.findById(username)).thenReturn(Optional.of(user))

        // when
        val result: UserDetails = authService.loadUserByUsername(username)

        // then
        assertNotNull(result)
    }

    @Test
    @DisplayName("사용자 조회 실패 - 아이디에 해당하는 유저 없음")
    fun loadUserByUsernameFail() {
        // given
        val username: String = "test1535@test.com"

        Mockito.`when`(userRepository.findById(username)).thenReturn(Optional.empty())

        // when & then
        assertThrows<BusinessException> { authService.loadUserByUsername(username) }
    }

    @Test
    @DisplayName("회원가입 성공")
    fun register() {
        // given
        val registerRequest: RegisterRequest = RegisterRequest(
            email = "test123@test.com",
            password = "Password123!!",
            name = "테스터",
            phone = "010-1234-5678"
        )

        Mockito.`when`(userRepository.existsById(registerRequest.email!!)).thenReturn(false)
        Mockito.`when`(passwordEncoder.encode(registerRequest.password)).thenReturn("encodedPassword")
        Mockito.`when`(jwtProvider.createToken(registerRequest.email!!)).thenReturn("token")

        // when
        val result: TokenResponse = authService.register(registerRequest)

        // then
        assertNotNull(result)
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    fun registerFailAlreadyExists() {
        // given
        val registerRequest: RegisterRequest = RegisterRequest(
            email = "test123@test.com",
            password = "Password123!!",
            name = "테스터",
            phone = "010-1234-5678"
        )

        Mockito.`when`(userRepository.existsById(registerRequest.email!!)).thenReturn(true)

        // when & then
        assertThrows<BusinessException> { authService.register(registerRequest) }
    }

    @Test
    @DisplayName("로그인 성공")
    fun login() {
        //given
        val loginRequest: LoginRequest = LoginRequest(
            email = "test123@test.com",
            password = "Password123!!",
        )

        Mockito.`when`(userRepository.findById(loginRequest.email!!)).thenReturn(
            Optional.of(
                User(
                    email = loginRequest.email!!,
                    password = "encodedPassword",
                    name = "테스터",
                    phone = "010-1234-5678"
                )
            )
        )
        Mockito.`when`(passwordEncoder.matches(loginRequest.password, "encodedPassword")).thenReturn(true)

        Mockito.`when`(jwtProvider.createToken(loginRequest.email!!)).thenReturn("token")

        //when
        val result: TokenResponse = authService.login(loginRequest)

        //then
        assertNotNull(result)
    }

    @Test
    @DisplayName("로그인 실패 - ID에 해당하는 유저 없음")
    fun loginFailNotFound() {
        //given
        val loginRequest: LoginRequest = LoginRequest(
            email = "test123@test.com",
            password = "Password123!!",
        )

        Mockito.`when`(userRepository.findById(loginRequest.email!!)).thenReturn(Optional.empty())

        //when & then
        assertThrows<BusinessException> { authService.login(loginRequest) }
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    fun loginFailInvalidPassword() {
        //given
        val loginRequest: LoginRequest = LoginRequest(
            email = "test123@test.com",
            password = "Password123!!",
        )

        Mockito.`when`(userRepository.findById(loginRequest.email!!)).thenReturn(
            Optional.of(
                User(
                    email = loginRequest.email!!,
                    password = "encodedPassword",
                    name = "테스터",
                    phone = "010-1234-5678"
                )
            )
        )
        Mockito.`when`(passwordEncoder.matches(loginRequest.password, "encodedPassword")).thenReturn(false)

        //when & then
        assertThrows<BusinessException> { authService.login(loginRequest) }
    }
}
