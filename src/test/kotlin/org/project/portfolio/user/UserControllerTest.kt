package org.project.portfolio.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.auth.dto.LoginRequest
import org.project.portfolio.auth.dto.TokenResponse
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import kotlin.test.assertContains
import kotlin.test.assertNotNull

@DisplayName("UserController 테스트")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    lateinit var mvc: MockMvc


    @Test
    @DisplayName("내 정보 조회 API 테스트 - 성공")
    fun getUserInfo() {
        // when
        val result: MvcResult = mvc.perform(
            get("/api/v1/user")
                .header("Authorization", getAccessToken())
                .contentType("application/json")
        ).andReturn()

        // then
        assertEquals(200, result.response.status)
        assertNotNull(result.response.contentAsString)

    }

    @Test
    @DisplayName("내 정보 조회 API 테스트 - 실패(토큰 없음)")
    fun getUserInfoFailTokenNotFound() {
        // when
        val result: MvcResult = mvc.perform(
            get("/api/v1/user")
                .contentType("application/json")
        ).andReturn()

        // then
        assertEquals(401, result.response.status)
        assertContains(result.response.contentAsString, ErrorCode.TOKEN_NOT_FOUND.message)
    }

    @Test
    @DisplayName("내 정보 조회 API 테스트 - 실패(토큰 만료)")
    fun getUserInfoFailTokenExpired() {
        // given
        /** 만료된 토큰 */
        val token: String =
            "Bearer eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwb3J0Zm9saW8iLCJpYXQiOjE3MjE3NDAwMDAsImV4cCI6MTcyMTc0MTgwMCwic3ViIjoidGVzdEB0ZXN0LmNvbSJ9.04z2PmbXY6cbp8GLJH4OEcfx_exG3OploOtytZanJxVJvl716gA56MAKO2LmUhhQ"
        // when
        val result: MvcResult = mvc.perform(
            get("/api/v1/user")
                .header("Authorization", token)
                .contentType("application/json")
        ).andReturn()

        // then
        assertEquals(401, result.response.status)
        assertContains(result.response.contentAsString, ErrorCode.TOKEN_EXPIRED.message)
    }


    private fun getAccessToken(): String {
        val loginRequest: LoginRequest = LoginRequest(
            email = "test@test.com",
            password = "Password1234~!"
        )

        mvc.perform(
            post("/api/v1/auth/login")
                .content(ObjectMapper().writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().let {
            return ObjectMapper().readValue(it.response.contentAsString, TokenResponse::class.java).token
        }
    }
}
