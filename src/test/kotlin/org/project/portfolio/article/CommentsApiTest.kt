package org.project.portfolio.article

import ArticleBuilder
import CommentFixture
import UsersFixture
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.junit.jupiter.api.DisplayName
import org.project.portfolio.article.application.ArticleManageService
import org.project.portfolio.article.application.CommentService
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@DisplayName("Comments API 통합 테스트")
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CommentsApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
    private val articleService: ArticleManageService,
    private val commentService: CommentService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        val userTokens = mutableListOf<TokenResponse>()
        val articles = mutableListOf<ArticleResponse>()
        val comments = mutableListOf<CommentResponse>()

        beforeSpec {
            val userLength = 2
            UsersFixture.getRandomRegisterRequest(userLength).map { registerRequest ->
                val tokenResponse = authService.register(registerRequest)
                userTokens.add(tokenResponse)
            }

            val articleLength = 1
            ArticleBuilder.getRandomArticle(articleLength).mapIndexed { index, articleForm ->
                articles.add(
                    articleService.createArticle(
                        userId = index.toLong() + 1,
                        articleForm = articleForm,
                    ),
                )
            }

            val commentLength = 2
            CommentFixture.getRandomComment(commentLength).mapIndexed { index, commentForm ->
                comments.add(
                    commentService.createComment(
                        userId = index.toLong() + 1,
                        articleId = articles[0].id,
                        content = commentForm.content,
                    ),
                )
            }
        }

        describe("POST /api/v1/articles/{articleId}/comments - 댓글 작성 API") {
            it("201 Created") {
                mockMvc.post("/api/v1/articles/${articles[0].id}/comments") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "테스트 댓글"
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.content") { value("테스트 댓글") }
                }
            }

            it("400 Bad Request (@Valid)") {
                mockMvc.post("/api/v1/articles/${articles[0].id}/comments") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": ""
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("401 Unauthorized") {
                mockMvc.post("/api/v1/articles/${articles[0].id}/comments") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "테스트 댓글"
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isUnauthorized() }
                }
            }
        }

        describe("PATCH /api/v1/articles/{articleId}/comments/{commentId} - 댓글 수정 API") {
            it("200 OK") {
                mockMvc.patch("/api/v1/articles/${articles[0].id}/comments/${comments[0].id}") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "수정된 댓글"
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { value("수정된 댓글") }
                }
            }

            it("401 Unauthorized") {
                mockMvc.patch("/api/v1/articles/${articles[0].id}/comments/${comments[0].id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "수정된 댓글"
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            it("403 Forbidden (다른 사용자의 댓글)") {
                mockMvc.patch("/api/v1/articles/${articles[0].id}/comments/${comments[1].id}") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "수정된 댓글"
                        }
                    """.trimIndent()
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }

        describe("DELETE /api/v1/articles/{articleId}/comments/{commentId} - 댓글 삭제 API") {
            it("204 No Content") {
                mockMvc.delete("/api/v1/articles/${articles[0].id}/comments/${comments[0].id}") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("401 Unauthorized") {
                mockMvc.delete("/api/v1/articles/${articles[0].id}/comments/${comments[0].id}") {
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            it("403 Forbidden (다른 사용자의 댓글)") {
                mockMvc.delete("/api/v1/articles/${articles[0].id}/comments/${comments[1].id}") {
                    header("Authorization", "Bearer ${userTokens[0].token}")
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }
    },
)
