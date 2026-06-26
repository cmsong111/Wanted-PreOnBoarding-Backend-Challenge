package org.project.portfolio.article

import ArticleFixture
import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import org.project.portfolio.article.application.ArticleCommandService
import org.project.portfolio.article.presentation.request.ArticleForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.user.domain.User
import org.project.portfolio.utils.withJwt
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@Transactional
@DisplayName("Articles API 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ArticlesApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
    private val articleService: ArticleCommandService,
) : DescribeSpec(
    {
        val userRegisterForm = UsersFixture.getRandomRegisterRequest()
        val author: User = authService.register(userRegisterForm)
        val authorToken: TokenResponse = authService.login(userRegisterForm.email, userRegisterForm.password)

        val nonUserRegisterForm = UsersFixture.getRandomRegisterRequest()
        authService.register(nonUserRegisterForm)
        val nonAuthorToken: TokenResponse = authService.login(nonUserRegisterForm.email, nonUserRegisterForm.password)

        val article: ArticleResponse = articleService.createArticle(
            email = author.email,
            articleForm = ArticleFixture.getRandomArticle(author.id),
        )

        describe("GET /api/v1/articles - 게시글 조회 API") {
            it("200 OK - 전체 목록 조회") {
                mockMvc.get("/api/v1/articles") {
                    param("page", "0")
                    param("size", "10")
                    contentType = MediaType.APPLICATION_JSON
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.page.size") { value(10) }
                    jsonPath("$.page.number") { value(0) }
                    jsonPath("$.content") { isArray() }
                }
            }

            it("200 OK - 키워드 검색이 실제로 필터링된다") {
                val uniqueKeyword = "UNIQUE_SEARCH_KEYWORD_999"
                articleService.createArticle(
                    email = author.email,
                    articleForm = ArticleForm(
                        title = "제목 $uniqueKeyword",
                        content = "검색 테스트 본문",
                        tags = listOf("kotlin", "spring"),
                    ),
                )

                mockMvc.get("/api/v1/articles") {
                    param("keyword", uniqueKeyword)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpectAll {
                    status { isOk() }
                    jsonPath("$.content.length()") { value(1) }
                    jsonPath("$.content[0].title") { value("제목 $uniqueKeyword") }
                    jsonPath("$.content[0].tags[0]") { value("kotlin") }
                }
            }
        }

        describe("POST /api/v1/articles - 게시글 작성 API") {
            it("201 Created - 이미지 및 태그 포함") {
                val title = "테스트 제목"
                val content = "테스트 내용"
                val file = MockMultipartFile(
                    "images",
                    "image.jpg",
                    "image/jpeg",
                    "fake-image-content".toByteArray(),
                )

                mockMvc.perform(
                    multipart("/api/v1/articles")
                        .file(file)
                        .param("title", title)
                        .param("content", content)
                        .param("tags", "spring", "kotlin")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpectAll(
                    status().isCreated,
                    jsonPath("$.title").value(title),
                    jsonPath("$.tags[0]").value("spring"),
                    jsonPath("$.tags[1]").value("kotlin"),
                )
            }

            it("400 Bad Request (@Valid)") {
                mockMvc.perform(
                    multipart("/api/v1/articles")
                        .param("title", "")
                        .param("content", "내용")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpectAll(
                    status().isBadRequest,
                )
            }

            it("401 Unauthorized") {
                mockMvc.perform(
                    multipart("/api/v1/articles")
                        .param("title", "제목")
                        .param("content", "내용")
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpectAll(
                    status().isUnauthorized,
                )
            }
        }

        describe("GET /api/v1/articles/{articleId} - 게시글 상세 조회 API") {
            it("200 OK") {
                mockMvc.get("/api/v1/articles/${article.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }

                    jsonPath("$.id") { value(article.id) }
                    jsonPath("$.title") {
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.content") {
                        isString()
                        isNotEmpty()
                    }
                    jsonPath("$.createdAt") { isNotEmpty() }
                    jsonPath("$.updatedAt") { isNotEmpty() }
                    jsonPath("$.author") { isMap() }
                    jsonPath("$.comments") { isArray() }
                    jsonPath("$.images") { isArray() }
                    jsonPath("$.tags") { isArray() }
                }
            }

            it("404 Not Found") {
                val articleId = Int.MAX_VALUE
                mockMvc.get("/api/v1/articles/$articleId") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpectAll {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }

        describe("PATCH /api/v1/articles/{articleId} - 게시글 수정 API") {
            it("200 OK") {
                val title = "수정된 제목"
                val content = "수정된 내용"
                val file = MockMultipartFile(
                    "images",
                    "updated-image.jpg",
                    "image/jpeg",
                    "updated-image-content".toByteArray(),
                )

                mockMvc.perform(
                    multipart("/api/v1/articles/${article.id}")
                        .file(file)
                        .param("title", title)
                        .param("content", content)
                        .param("tags", "updated-tag")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectAll(
                    status().isOk,
                    jsonPath("$.title").value(title),
                    jsonPath("$.tags[0]").value("updated-tag"),
                )
            }

            it("400 Bad Request (@Valid)") {
                mockMvc.perform(
                    multipart("/api/v1/articles/${article.id}")
                        .param("title", "")
                        .param("content", "수정된 내용")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectAll(
                    status().isBadRequest,
                )
            }

            it("401 Unauthorized") {
                mockMvc.perform(
                    multipart("/api/v1/articles/${article.id}")
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectAll(
                    status().isUnauthorized,
                )
            }

            it("404 Not Found") {
                mockMvc.perform(
                    multipart("/api/v1/articles/${Int.MAX_VALUE}")
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectAll(
                    status().isNotFound,
                )
            }
        }

        describe("DELETE /api/v1/articles/{articleId} - 게시글 삭제 API") {
            it("204 No Content") {
                mockMvc.perform(
                    delete("/api/v1/articles/${article.id}")
                        .withJwt(authorToken),
                ).andExpectAll(
                    status().isNoContent,
                )
            }

            it("401 Unauthorized") {
                mockMvc.delete("/api/v1/articles/${article.id}")
                    .andExpect {
                        status { isUnauthorized() }
                    }
            }

            it("403 Forbidden") {
                mockMvc.perform(
                    delete("/api/v1/articles/${article.id}")
                        .withJwt(nonAuthorToken),
                ).andExpectAll(
                    status().isForbidden,
                )
            }

            it("404 Not Found") {
                mockMvc.perform(
                    delete("/api/v1/articles/${Int.MAX_VALUE}")
                        .withJwt(authorToken),
                ).andExpectAll(
                    status().isNotFound,
                )
            }
        }
    },
)
