package org.project.portfolio

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("PortfolioApplication 테스트")
@SpringBootTest
class PortfolioApplicationTests {

    @Test
    @DisplayName("Context 로드 테스트")
    fun contextLoads() {
        assertDoesNotThrow {
            // Context 로드 테스트
        }
    }

}
