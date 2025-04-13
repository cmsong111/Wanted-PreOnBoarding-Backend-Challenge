package org.project.portfolio.article

import ArticleBuilder
import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.project.portfolio.article.application.ArticleManageService
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
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
    private val articleService: ArticleManageService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        val userTokens = mutableListOf<TokenResponse>()
        val articleIds = mutableListOf<Long>()

        beforeSpec {
            // 1~10번 유저의 명의로 게시글 30개 작성
            val userLength = 10
            userTokens.addAll(
                UsersFixture.getRandomRegisterRequest(userLength).map { registerForm ->
                    authService.register(registerForm)
                },
            )

            val articleLength = 30
            articleIds.addAll(
                ArticleBuilder.getRandomArticle(articleLength).mapIndexed { index, articleForm ->
                    articleService.createArticle(
                        userId = index % userLength + 1L,
                        articleForm = articleForm,
                    ).id
                },
            )
        }

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
                        .header("Authorization", "Bearer ${userTokens[0].token}")
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
                        .header("Authorization", "Bearer ${userTokens[0].token}")
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
                // Given
                val articleId = articleIds[0]

                // When & Then
                mockMvc.get("/api/v1/articles/$articleId") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }

                    jsonPath("$.id") { value(articleId) }
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
                val articleId = 9999

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
                val articleId = articleIds[0]
                val title = "수정된 제목"
                val content = "수정된 내용"
                val file = MockMultipartFile(
                    "images",
                    "updated-image.jpg",
                    "image/jpeg",
                    "updated-image-content".toByteArray(),
                )

                mockMvc.perform(
                    multipart("/api/v1/articles/$articleId")
                        .file(file)
                        .param("title", title)
                        .param("content", content)
                        .header("Authorization", "Bearer ${userTokens[0].token}")
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
                val articleId = articleIds[0]
                mockMvc.perform(
                    multipart("/api/v1/articles/$articleId")
                        .param("title", "") // 빈 제목
                        .param("content", "수정된 내용")
                        .header("Authorization", "Bearer ${userTokens[0].token}")
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
                val articleId = articleIds[0]
                mockMvc.perform(
                    multipart("/api/v1/articles/$articleId")
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
                    multipart("/api/v1/articles/9999")
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .header("Authorization", "Bearer ${userTokens[0].token}")
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
                val articleId = articleIds[0]
                mockMvc.delete("/api/v1/articles/$articleId") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                }.andExpect {
                    status { isOk() }
                }
            }

            it("401 Unauthorized") {
                val articleId = articleIds[0]
                mockMvc.delete("/api/v1/articles/$articleId")
                    .andExpect {
                        status { isUnauthorized() }
                    }
            }

            it("403 Forbidden") {
                val articleId = articleIds[0]
                mockMvc.delete("/api/v1/articles/$articleId") {
                    header("Authorization", "Bearer ${userTokens[1].token}") // 다른 사용자의 토큰
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("404 Not Found") {
                mockMvc.delete("/api/v1/articles/9999") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }
    },
)
