package org.project.portfolio

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@DisplayName("PortfolioApplication 테스트")
@SpringBootTest
@ActiveProfiles("test")
class PortfolioApplicationTests {
    @Test
    @DisplayName("Context 로드 테스트")
    fun contextLoads() {
    }
}
