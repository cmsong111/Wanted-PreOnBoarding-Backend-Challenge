package org.project.portfolio.article

import ArticleFixture
import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.project.portfolio.article.application.ArticleAuthorService
import org.project.portfolio.article.application.ArticleReaderService
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.user.domain.User
import org.project.portfolio.utils.withJwt
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@Transactional
@DirtiesContext
@DisplayName("Articles API 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ArticlesApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
    private val articleService: ArticleAuthorService,
    private val articleReaderService: ArticleReaderService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        val userRegisterForm = UsersFixture.getRandomRegisterRequest()
        val author: User = authService.register(userRegisterForm)
        val authorToken: TokenResponse = authService.login(userRegisterForm.email, userRegisterForm.password)

        val nonUserRegisterForm = UsersFixture.getRandomRegisterRequest()
        val nonAuthor: User = authService.register(nonUserRegisterForm)
        val nonAuthorToken: TokenResponse = authService.login(nonUserRegisterForm.email, nonUserRegisterForm.password)

        val article: ArticleResponse = articleService.createArticle(
            email = author.email,
            articleForm = ArticleFixture.getRandomArticle(author.id),
        )

        describe("GET /api/v1/articles - 게시글 조회 API") {
            it("200 OK") {
                mockMvc.get("/api/v1/articles") {
                    param("page", "0")
                    param("size", "10")
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status().isOk
                    content().contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.page.size").value(10)
                    jsonPath("$.page.number").value(0)
                    jsonPath("$.content").isArray
                }
            }
        }

        describe("POST /api/v1/articles - 게시글 작성 API") {
            it("201 Created") {
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
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpect {
                    status().isCreated
                }
            }
            it("400 Bad Request (@Valid)") {
                mockMvc.perform(
                    multipart("/api/v1/articles")
                        .param("title", "") // 빈 제목
                        .param("content", "내용")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpect {
                    status().isBadRequest
                }
            }
            it("401 Unauthorized") {
                mockMvc.perform(
                    multipart("/api/v1/articles")
                        .param("title", "제목")
                        .param("content", "내용")
                        .contentType(MediaType.MULTIPART_FORM_DATA),
                ).andExpect {
                    status().isUnauthorized
                }
            }
        }

        describe("GET /api/v1/articles/{articleId} - 게시글 조회 API") {
            it("200 OK") {
                // When & Then
                mockMvc.get("/api/v1/articles/${article.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
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
                }
            }

            it("404 Not Found") {
                // Given
                val articleId = Int.MAX_VALUE
                // When & Then
                mockMvc.get("/api/v1/articles/$articleId") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
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
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpect {
                    status().isOk
                }
            }

            it("400 Bad Request (@Valid)") {
                mockMvc.perform(
                    multipart("/api/v1/articles/${article.id}")
                        .param("title", "") // 빈 제목
                        .param("content", "수정된 내용")
                        .withJwt(authorToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpect {
                    status().isBadRequest
                }
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
                ).andExpect {
                    status().isUnauthorized
                }
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
                ).andExpect {
                    status().isNotFound
                }
            }
        }

        describe("DELETE /api/v1/articles/{articleId} - 게시글 삭제 API") {
            it("200 OK") {
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
