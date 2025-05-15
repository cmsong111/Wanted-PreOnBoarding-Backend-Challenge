package org.project.portfolio.article

import ArticleFixture
import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.project.portfolio.article.application.ArticleAuthorService
import org.project.portfolio.article.application.CommentService
import org.project.portfolio.article.presentation.request.CommentForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.user.domain.User
import org.project.portfolio.utils.prettyJson
import org.project.portfolio.utils.withJwt
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@DisplayName("Comments API 통합 테스트")
@Transactional
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CommentsApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
    private val articleService: ArticleAuthorService,
    private val commentService: CommentService,
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

        val comment: CommentResponse = commentService.createComment(
            email = author.email,
            articleId = article.id,
            content = "테스트 댓글",
        )

        describe("POST /api/v1/articles/{articleId}/comments - 댓글 작성 API") {
            it("201 Created") {
                mockMvc.post("/api/v1/articles/${article.id}/comments") {
                    withJwt(authorToken)
                    contentType = MediaType.APPLICATION_JSON
                    content = CommentForm("테스트 댓글").prettyJson()
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.content") { value("테스트 댓글") }
                }
            }

            it("400 Bad Request (@Valid)") {
                mockMvc.post("/api/v1/articles/${article.id}/comments") {
                    withJwt(authorToken)
                    contentType = MediaType.APPLICATION_JSON
                    content = CommentForm("").prettyJson()
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("401 Unauthorized") {
                mockMvc.post("/api/v1/articles/${article.id}/comments") {
                    contentType = MediaType.APPLICATION_JSON
                    content = CommentForm("테스트 댓글").prettyJson()
                }.andExpect {
                    status { isUnauthorized() }
                }
            }
        }

        describe("PATCH /api/v1/articles/{articleId}/comments/{commentId} - 댓글 수정 API") {
            it("200 OK") {
                mockMvc.patch("/api/v1/articles/${article.id}/comments/${comment.id}") {
                    withJwt(authorToken)
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
                mockMvc.patch("/api/v1/articles/${article.id}/comments/${comment.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """
                        {
                            "content": "수정된 댓글"
                        }
                    """.trimIndent()
                }.andExpectAll {
                    status { isUnauthorized() }
                }
            }

            it("403 Forbidden (다른 사용자의 댓글)") {
                mockMvc.patch("/api/v1/articles/${article.id}/comments/${comment.id}") {
                    withJwt(nonAuthorToken)
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
                mockMvc.delete("/api/v1/articles/${article.id}/comments/${comment.id}") {
                    withJwt(authorToken)
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("401 Unauthorized") {
                mockMvc.delete("/api/v1/articles/${article.id}/comments/${comment.id}") {
                }.andExpect {
                    status { isUnauthorized() }
                }
            }

            it("403 Forbidden (다른 사용자의 댓글)") {
                mockMvc.delete("/api/v1/articles/${article.id}/comments/${comment.id}") {
                    withJwt(nonAuthorToken)
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }
    },
)
