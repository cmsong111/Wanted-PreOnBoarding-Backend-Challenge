package org.project.portfolio

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.kotest.core.spec.style.DescribeSpec

class ArchitectureTest :
    DescribeSpec({
        val classes = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("org.project.portfolio")

        describe("3계층 아키텍처 의존성 규칙") {
            it("domain 패키지는 application, presentation, infrastructure 패키지에 의존할 수 없다") {
                noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..",
                        "..presentation..",
                        "..infrastructure..",
                    )
                    .allowEmptyShould(true)
                    .check(classes)
            }

            it("article 도메인은 notification 도메인에 직접 의존하지 않는다 (이벤트로만 통신)") {
                noClasses()
                    .that().resideInAPackage("org.project.portfolio.article..")
                    .should().dependOnClassesThat().resideInAPackage("org.project.portfolio.notification..")
                    .check(classes)
            }
        }
    })
