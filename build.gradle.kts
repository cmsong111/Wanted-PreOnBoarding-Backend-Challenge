import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("java-test-fixtures")
    id("jacoco")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.sonarqube") version "6.0.1.5171"
    id("com.gorylenko.gradle-git-properties") version "2.5.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "org.project"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.5")

    val springCloudAwsVersion = "3.3.0"
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:$springCloudAwsVersion"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("com.h2database:h2")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    val jwtVersion = "0.12.6"
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    val openApiVersion = "2.8.6"
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    val kotestVersion = "5.9.0"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.17")

    val fixtureMonkeyVersion = "1.1.11"
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:$fixtureMonkeyVersion")
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:$fixtureMonkeyVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootBuildImage>().configureEach {
    createdDate = "now"
}

configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            useVersion("2.15.3")
        }
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "cmsong111_Wanted-PreOnBoarding-Backend-Challenge")
        property("sonar.organization", "cmsong111")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/kotlin")
        property("sonar.tests", "src/test/kotlin")
        property("sonar.java.binaries", "build/classes/kotlin/main")
        property("sonar.java.test.binaries", "build/classes/kotlin/test")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "./SwaggerConfig.kt, **/entity/**, **/dto/**, **/config/**")
    }
}
