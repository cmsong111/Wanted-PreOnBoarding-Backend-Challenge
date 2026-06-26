package org.project.portfolio

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("PortfolioApplication 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PortfolioApplicationTests :
    FunSpec(
        {
            test("Load application context") {
                assert(true)
            }
        },
    )
