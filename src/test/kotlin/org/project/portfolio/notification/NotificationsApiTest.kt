package org.project.portfolio.notification

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@DisplayName("Notifications API 통합 테스트")
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationsApiTest {
    @Nested
    @DisplayName("POST /api/v1/auth/register")
    inner class GetArticles {
        @Test
        @DisplayName("200 OK")
        fun success() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }

        @Test
        @DisplayName("400 Bad Request (이미 존재하는 이메일)")
        fun badRequestExistsEmail() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }

        @Test
        @DisplayName("400 Bad Request (@Valid)")
        fun badRequest() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    inner class PostLogin {
        @Test
        @DisplayName("200 OK")
        fun success() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }

        @Test
        @DisplayName("400 Bad Request (잘못된 요청)")
        fun badRequest() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/email-check")
    inner class PostEmailCheck {
        @Test
        @DisplayName("200 OK")
        fun success() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }

        @Test
        @DisplayName("400 Bad Request (잘못된 요청)")
        fun badRequest() {
            // given
            // when
            // then
            Assertions.assertTrue(true)
        }
    }
}
