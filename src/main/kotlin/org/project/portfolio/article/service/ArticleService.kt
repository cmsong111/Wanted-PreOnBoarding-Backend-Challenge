package org.project.portfolio.article.service

import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {

    /**
     * 게시글 전체 조회 메소드
     */
    fun getArticles(): List<Article> {
        return articleRepository.findAll()
    }

    /**
     * 게시글 단일 조회 메소드
     * @param id 게시글 ID
     */
    fun getArticle(id: Long): Article {
        return articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
    }

    /**
     * 게시글 생성 메소드
     * @param username 유저 이름
     * @articleRequest 게시글 요청 DTO
     */
    fun createArticle(username: String, articleRequest: ArticleRequest): Article {
        val user: User = userRepository.findById(username).orElseThrow() {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val article = Article(
            title = articleRequest.title!!,
            content = articleRequest.content!!,
            author = user
        )
        return articleRepository.save(article)
    }

    /**
     * 게시글 수정 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 수정 가능
     * @param id 게시글 ID
     * @param articleRequest 게시글 요청 DTO
     */
    fun updateArticle(id: Long, articleRequest: ArticleRequest): Article {
        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 게시글 수정
        article.update(articleRequest.title!!, articleRequest.content!!)

        // 게시글 저장 및 반환
        return articleRepository.save(article)
    }

    /**
     * 게시글 삭제 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 삭제 가능
     * @param id 게시글 ID
     */
    fun deleteArticle(id: Long) {
        // 게시글 조회
        articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
        // 게시글 삭제
        articleRepository.deleteById(id)
    }
}
