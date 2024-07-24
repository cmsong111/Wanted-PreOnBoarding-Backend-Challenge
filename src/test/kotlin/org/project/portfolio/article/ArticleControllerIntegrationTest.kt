package org.project.portfolio.article

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertContains
import kotlin.test.assertNotNull

@DisplayName("Article Controller 통합 테스트")
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerIntegrationTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @Test
    @DisplayName("게시글 전체 조회")
    fun getArticles() {
        val result = mvc.perform(get("/api/v1/article"))
            .andExpect(status().isOk)
            .andReturn()

        assertNotNull(result.response.contentAsString)
    }

    @Test
    @DisplayName("게시글 상세 조회")
    fun getArticle() {
        val result = mvc.perform(get("/api/v1/article/1"))
            .andExpect(status().isOk)
            .andReturn()

        assertNotNull(result.response.contentAsString)
    }

    @Test
    @DisplayName("게시글 상세 조회- 실패 없는 게시글")
    fun getArticleFail() {
        val result = mvc.perform(get("/api/v1/article/9999"))
            .andExpect(status().isNotFound)
            .andReturn()

        assertContains(result.response.contentAsString, ErrorCode.ARTICLE_NOT_FOUND.message)
    }

    @Test
    @Transactional
    @DisplayName("게시글 생성(일반 유저) - 성공")
    fun createArticleSuccess() {
        val articleRequest = ArticleRequest(
            title = "제목입니다",
            content = "내용입니다"
        )

        val result = mvc.perform(
            post("/api/v1/article")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)
            .andReturn()
        println(result.response.contentAsString)
        assertNotNull(result.response.contentAsString)
    }

    @Test
    @DisplayName("게시글 생성 제목 200글자 초과 - 실패")
    fun createArticleTitleFail() {
        val articleRequest = ArticleRequest(
            title = makeString(201),
            content = "내용입니다"
        )

        val result = mvc.perform(
            post("/api/v1/article")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()

        assertNotNull(result.response.contentAsString)
    }

    @Test
    @DisplayName("게시글 생성 내용 2000글자 초과 - 실패")
    fun createArticleContentFail() {
        val articleRequest = ArticleRequest(
            title = "제목입니다",
            content = makeString(2001)
        )

        val result = mvc.perform(
            post("/api/v1/article")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()

        assertNotNull(result.response.contentAsString)
    }

    /** 길이만큼 랜덤한 문자열 생성 */
    private fun makeString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    @Test
    @DisplayName("게시글 생성(토큰 없음) - 실패")
    fun createArticleFailNoToken() {
        val articleRequest = ArticleRequest(
            title = "제목입니다",
            content = "내용입니다"
        )

        val result = mvc.perform(
            post("/api/v1/article")
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
            .andReturn()

        assertContains(result.response.contentAsString, ErrorCode.TOKEN_NOT_FOUND.message)
    }

    @Test
    @Transactional
    @DisplayName("게시글 수정 작성자 - 성공")
    fun updateArticleAuthorSuccess() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )

        val result = mvc.perform(
            patch("/api/v1/article/12")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }


    @Test
    @Transactional
    @DisplayName("게시글 수정 관리자 - 성공")
    fun updateArticleAdminSuccess() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )

        val result = mvc.perform(
            patch("/api/v1/article/12")
                .header("Authorization", createJwtToken("test@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("게시글 수정 토큰 없음 - 실패")
    fun updateArticleFailNoToken() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )

        val result = mvc.perform(
            patch("/api/v1/article/12")
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
            .andReturn()
    }

    @Test
    @DisplayName("게시글 수정 타 사용자 - 실패")
    fun updateArticleFailAnotherUser() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )

        val result = mvc.perform(
            patch("/api/v1/article/5")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden)
            .andReturn()
    }

    @Test
    @DisplayName("게시글 수정 작성자 10일 지남 - 실패")
    fun updateArticleAuthorAfter10DaysFail() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )
        // 11번은 2000-03-11에 작성됨
        val result = mvc.perform(
            patch("/api/v1/article/11")
                .header("Authorization", createJwtToken("user@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden)
            .andReturn()
    }


    @Test
    @DisplayName("게시글 수정 관리자 10일 지남 - 성공")
    fun updateArticleAdminAfter10DaysSuccess() {
        val articleRequest = ArticleRequest(
            title = "수정 제목입니다",
            content = "수정 내용입니다"
        )
        // 11번은 2000-03-11에 작성됨
        val result = mvc.perform(
            patch("/api/v1/article/1")
                .header("Authorization", createJwtToken("test@test.com"))
                .content(ObjectMapper().writeValueAsString(articleRequest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andReturn()
    }


    @Test
    @Transactional
    @DisplayName("게시글 삭제 작성자 - 성공")
    fun deleteArticlAuthorSuccess() {
        mvc.perform(
            delete("/api/v1/article/12")
                .header("Authorization", createJwtToken("user@test.com"))
        ).andExpect(status().isNoContent)
    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 관리자 권한으로 - 성공")
    fun deleteArticleAdminSuccess() {
        mvc.perform(
            delete("/api/v1/article/12")
                .header("Authorization", createJwtToken("test@test.com"))
        ).andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("게시글 삭제 토큰 없음 - 실패")
    fun deleteArticleFailNoToken() {
        mvc.perform(
            delete("/api/v1/article/12")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("게시글 삭제 타 사용자 - 실패")
    fun deleteArticleFailAnotherUser() {
        mvc.perform(
            delete("/api/v1/article/5")
                .header("Authorization", createJwtToken("user@test.com"))
        ).andExpect(status().isForbidden)
    }

    private fun createJwtToken(username: String): String {
        return jwtProvider.createToken(username)
    }

}
