package org.project.portfolio

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DisplayName("PortfolioApplication 테스트")
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PortfolioApplicationTests : FunSpec(
    {
        extensions(SpringExtension)

        test("Load application context") {
            assert(true)
        }
    },
)
