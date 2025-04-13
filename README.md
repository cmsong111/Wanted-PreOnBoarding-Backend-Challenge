# 📝 포트폴리오 프로젝트

[![Test CI with Gradle](https://github.com/cmsong111/Wanted-PreOnBoarding-Backend-Challenge/actions/workflows/build-and-test.yaml/badge.svg)](https://github.com/cmsong111/Wanted-PreOnBoarding-Backend-Challenge/actions/workflows/build-and-test.yaml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cmsong111_Wanted-PreOnBoarding-Backend-Challenge&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=cmsong111_Wanted-PreOnBoarding-Backend-Challenge)
[![Push to Docker Hub](https://img.shields.io/badge/Docker%20Hub-cmsong111%2Fportfolio%20server-%232496ED.svg?&style=flat&logo=docker&logoColor=white)](https://hub.docker.com/r/cmsong111/portfolio-server/)
[![pages-build-deployment](https://img.shields.io/badge/Dokka-Github%20Page-%237F52FF.svg?&style=flat&logo=kotlin&logoColor=white)](https://cmsong111.github.io/Wanted-PreOnBoarding-Backend-Challenge/)
> 원티드 8월 프리온보딩 백엔드 챌린지

## 설명

* 포트폴리오 프로젝트는 가장 기본 기능을 기반으로 본인만의 포트폴리오 프로젝트를 만들어보세요!
* 프로젝트 명칭은 변경해주시길 바랍니다!


* CRUD(Create, Read, Update, Delete) 데이터 조작 작업을 포함합니다.
* TEST코드 작성 및 단위 테스트를 진행합니다.
* 스프링 시큐리티를 활용합니다.

아래의 작업들은 필수로 구현해야하는 프로젝트 기능이며,

**본인만의 프로젝트 기능을 아래의 내용에 추가해서 작성해주세요!**

## 로그인

* [x]  스프링 시큐리티를 활용한 로그인 기능 구현
* [x]  테스트 코드 작성 및 통과

## 회원가입

* [x]  이메일 - 이메일 형식에 맞는지 검증
* [x]  휴대폰 번호 - 숫자와 하이폰으로 구성된 형식 검즘
* [x]  작성자 - 아이디 대소문자 및 한글 이름 검즘
* [x]  비밀번호 - 대소문자, 숫자 5개 이상, 특수문자 포함 2개 이상 검즘
* [x]  테스트 코드 작성 및 통과

## 게시글 등록

* [x]  제목 - 200글자 이하 제한
* [x]  내용 - 1000글자 이하 제한
* [x]  생성및 수정 시간 자동관리
* [x]  테스트 코드 작성 및 통과

## 게시글 수정

* [x]  생성일 기준 10일 이후 수정불가
* [x]  생성일 9일째 경고 알림(하루 후 수정 불가 알람)
* [x]  테스트 코드 작성 및 통과

## 게시글 목록조회

* [x]  생성일 기준 내림차순 오름차순 정렬
* [x]  title 기준 부분 검색 가능
* [x]  title 이 없을 경우 cratedAt 정렬 기준으로 표시
* [x]  deletedAt 기준 삭제된 게시글 제외
* [ ]  테스트 코드 작성 및 통과

## 게시글 상세보기

* [x]  수정 가능일 현재 날짜 기준 계산 및 표시
* [ ]  테스트 코드 작성 및 통과

## 게시글 삭제

* [x]  Soft Delete 적용 deletedAt 사용하여 삭제처리
* [x]  Hard Delete 적용
* [ ]  테스트 코드 작성 및 통과

## 📌 추가 기능구현

### Swagger 적용

* [x]  Swagger를 이용한 API 문서화

### 사용자 인증 및 권한 관리

* [x]  JWT를 이용한 사용자 인증
* [x]  게시글 작성자만 수정 및 삭제 가능
* [x]  관리자는 모든 게시글 수정 및 삭제 가능

### 파일 업로드 기능

* [ ]  게시글에 이미지 첨부 기능 추가
* [ ]  이미지 파일 형식 크기 제한
* [ ]  이미지 업로드 시 S3와 같은 외부 스토리지 연동

### 댓글 기능

게시글에 댓글 추가 기능

* [x]  댓글 작성, 수정, 삭제 (Soft Delete)
* [x]  댓글 작성자는 본인의 댓글만 수정 및 삭제 가능

### 좋아요 및 조회수 기능

* [x]  게시글 조회수 증가 기능
* [x]  동일 사용자가 여러 번 조회 시 조회수 증가 방지

### 알림 기능

* [ ]  댓글 및 좋아요 시 알림 기능
* [ ]  수정 제한 경고 알림

### 코드 리뷰

* [x] SonarCloud를 통한 정적 코드 분석
* [x] Jacoco를 통한 코드 커버리지 측정

# Open API 문서 작성

프론트엔드에서 사용할 API 문서를 작성하기 위해 Swagger를 적용합니다.

특히, Code Jen 같이 문서를 기반으로 코드를 생성하는 FE 개발자들도 있기 때문에, 특히나 API 문서작성에 신경을 써야합니다.

개인적으로 가장 중요하게 여기는 것은 `Security Scheme`, `Version`, `Last Commit Information` 입니다.

## Last Commit Information

마지막 커밋 정보를 API 문서에 표시합니다.

해당 정보를 통해 자동으로 업데이트 되는 문서를 확인할 수 있습니다.

Gradle Git Plugin을 사용하여 커밋 정보를 가져옵니다.

```gradle.kts
plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}
```

이후 Gradle로 빌드하게되면 `build/resources/main/git.properties` 파일이 생성됩니다.

```properties
git.branch=main
git.build.host=MacBook-Pro.local
git.build.user.email=cmsong111@naver.com
git.build.user.name=Namju Kim
git.build.version=0.0.1-SNAPSHOT
git.closest.tag.commit.count=
git.closest.tag.name=
git.commit.id=8764c86caab23d84f730181aa981773b5ef95b5c
git.commit.id.abbrev=8764c86
git.commit.id.describe=
git.commit.message.full=Update README.md\n
git.commit.message.short=Update README.md
git.commit.time=2025-03-24T02\:29\:11+0900
git.commit.user.email=cmsong111@naver.com
git.commit.user.name=Namju Kim
git.dirty=true
git.remote.origin.url=https\://github.com/cmsong111/Wanted-PreOnBoarding-Backend-Challenge.git
git.tags=
git.total.commit.count=49
```

해당 정보를 Swagger 문서 작성에 활용합니다.
[최종 코드](#최종-코드)에서 확인할 수 있습니다.

### 적용 결과

<img width="750" alt="스크린샷 2025-03-24 오전 3 30 45" src="https://github.com/user-attachments/assets/da3a22dc-807a-4f0d-b279-0c87d7bb0e92" />

## Security Scheme

서버에서 제공하는 API들은 서로 다른 인증을 요구합니다

단순 Get 요청은 인증이 필요 없지만, Post, Patch, Delete 같은 요청은 인증이 필요합니다.

따라서 전역적인 설정이 아닌, 다음과 같이 API 별로 인증 방식을 설정해주는것이 바람직 합니다.

메서드마다 `@SecurityRequirement(name = BEARER_AUTH)` 를 통해 인증 방식을 설정합니다.

SpringDoc 클래스에서 BEARER_AUTH를 상수로 설정해두면, 사용하기 편리합니다.

```kotlin
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "3. Article", description = "The article API")
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회 API")
    fun getArticle(
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
    ): ResponseEntity<ArticleDetailResponse> {
        TODO("Not yet implemented")
    }

    @PostMapping(consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun createArticle(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<ArticleDetailResponse> {
        TODO("Not yet implemented")
    }
}
```

### 적용 결과

다음과 같이 API 마다 다른 인증 방식을 설정할 수 있습니다.

<img width="790" alt="스크린샷 2025-03-24 오전 3 28 20" src="https://github.com/user-attachments/assets/6955dc20-b910-46ba-bb02-40b9c3a57105" />



# 최종 코드

```kotlin
package org.project.portfolio.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.Instant

/** Swagger 설정 */
@Component
class SwaggerConfig(
    private val gitProperties: GitProperties,
) {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().components(
            Components().apply {
                securitySchemes(
                    mapOf(
                        BEARER_AUTH to securityScheme(),
                    ),
                )
            },
        ).info(info())
    }

    /**
     * Bearer Auth Security Scheme
     */
    private fun securityScheme(): SecurityScheme {
        return SecurityScheme()
            .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
            .description(
                """<h3>Bearer Auth</h3>
                <p>JWT Token을 발급받아 Header에 넣어 요청합니다.</p>
                <p>Bearer [JWT Token 값]</p>
                """.trimIndent(),
            )
            .scheme("bearer")
            .bearerFormat("JWT")
    }

    /**
     * OpenAPI Info
     */
    private fun info(): Info {
        return Info().apply {
            title = "Portfolio Article API"
            description = """<h2>Article API</h2>
                <p>You can See Full Source Code at <a target="_blank" href="${gitProperties.get("remote.origin.url")}">Github</a></p>
                <p>API Document is generated by Swagger</p>
                |<h4>Last Commit Information</h4>
                |<p><strong>Message:</strong> ${gitProperties.get("commit.message.full")}</p>
                |<p><strong>Commit ID:</strong> ${gitProperties.get("commit.id")}</p>
                |<p><strong>Commit Time:</strong> ${Instant.ofEpochMilli(gitProperties.get("commit.time").toLong())}</p>
                |<p><strong>Committed by:</strong> ${gitProperties.get("commit.user.name")} (${gitProperties.get("commit.user.email")})</p>
            """.trimMargin()
            termsOfService = ""
            contact = Contact().apply {
                name = "Namju Kim"
                email = "cmsong111@gmail.com"
            }
            version = gitProperties.get("build.version")
        }
    }

    companion object {
        // Auth 방식
        const val BEARER_AUTH: String = "Bearer Authentication"
    }
}
```
