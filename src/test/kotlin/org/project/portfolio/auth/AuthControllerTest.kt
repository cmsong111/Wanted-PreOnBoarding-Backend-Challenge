package org.project.portfolio.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.auth.dto.LoginRequest
import org.project.portfolio.auth.dto.RegisterRequest
import org.project.portfolio.auth.dto.TokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("AuthController 테스트")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    @DisplayName("로그인 API 테스트 - 성공")
    fun loginSuccess() {
        // given
        val loginRequest: LoginRequest = LoginRequest(
            email = "test@test.com",
            password = "Password1234~!"
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/login")
                .content(ObjectMapper().writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        val tokenResponse: TokenResponse = ObjectMapper().readValue(result.response.contentAsString, TokenResponse::class.java)

        // then
        assertEquals(200, result.response.status)
        assertNotNull(tokenResponse.token)
    }

    @Test
    @DisplayName("로그인 API 테스트 - 실패(잘못된 비밀번호)")
    fun loginFail() {
        // given
        val loginRequest: LoginRequest = LoginRequest(
            email = "test@test.com",
            password = "wrong_password1234~!"
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/login")
                .content(ObjectMapper().writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 성공")
    @Transactional
    fun registerSuccess() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest()

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        val tokenResponse: TokenResponse = ObjectMapper().readValue(result.response.contentAsString, TokenResponse::class.java)

        // then
        assertEquals(200, result.response.status)
        assertNotNull(tokenResponse.token)
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(이미 가입된 이메일)")
    fun registerFailDuplicatedEmail() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            email = "test@test.com"
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
        assertContains(result.response.contentAsString, "이미")
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(잘못된 이메일 양식)")
    fun registerFailInvalidEmail() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            email = "test",
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
        assertContains(result.response.contentAsString, "이메일")
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(유효하지 않은 전화번호)")
    fun registerFailInvalidPhone() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            phone = "I am not a phone number",
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
        assertContains(result.response.contentAsString, "전화번호")
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(이름이 영어, 한글로만 구성되지 않음)")
    fun registerFailInvalidName() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            name = "테스터123@*(&$(*@$&*(^<h1>"
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
        assertContains(result.response.contentAsString, "이름")
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(패스워드에 대소문자, 숫자 5개 이상, 특수문자 포함 2개 이상 포함되지 않음)")
    fun registerFailInvalidPassword() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            password = "password"
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
        assertContains(result.response.contentAsString, "비밀번호")
    }

    @Test
    @DisplayName("회원가입 API 테스트 - 실패(빈 값)")
    fun registerFailEmpty() {
        // given
        val registerRequest: RegisterRequest = makeRegisterRequest(
            email = "",
            password = "",
            phone = "",
            name = ""
        )

        // when
        val result: MvcResult = mvc.perform(
            post("/api/v1/auth/register")
                .content(ObjectMapper().writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        // then
        assertEquals(400, result.response.status)
    }

    /** 유저 생성 */
    private fun makeRegisterRequest(
        email: String = "test123@test.com",
        password: String = "Password12345!!",
        name: String = "테스터",
        phone: String = "010-1234-5678"
    ): RegisterRequest {
        return RegisterRequest(
            email = email,
            password = password,
            name = name,
            phone = phone
        )
    }
}
