package org.project.portfolio.article

import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.article.entity.Article
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.user.UserRepository
import org.project.portfolio.user.entity.Role
import org.project.portfolio.user.entity.User
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
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
     * @param username 유저 이름
     * @param id 게시글 ID
     * @param articleRequest 게시글 요청 DTO
     */
    fun updateArticle(username: String, id: Long, articleRequest: ArticleRequest): Article {
        // 유저 조회
        val user: User = userRepository.findById(username).orElseThrow() {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 게시글 작성자와 요청자가 다를 경우 예외 발생
        // TODO: Checker 클래스를 만들어서 중복 코드 제거
        if (article.author != user && !user.authorities.contains(Role.ADMIN)) {
            throw BusinessException(ErrorCode.ARTICLE_AUTHOR_NOT_MATCH)
        }

        // 게시글 수정
        article.update(articleRequest.title!!, articleRequest.content!!)

        // 게시글 저장 및 반환
        return articleRepository.save(article)
    }

    /**
     * 게시글 삭제 메소드
     * @param username 유저 이름
     * @param id 게시글 ID
     */
    fun deleteArticle(username: String, id: Long) {
        // 유저 조회
        val user: User = userRepository.findById(username).orElseThrow() {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 게시글 작성자와 요청자가 다를 경우 예외 발생
        // TODO: Checker 클래스를 만들어서 중복 코드 제거\
        println(article)
        println(user)

        if (article.author != user && !user.authorities.contains(Role.ADMIN)) {
            throw BusinessException(ErrorCode.ARTICLE_AUTHOR_NOT_MATCH)
        }
        // 게시글 삭제
        articleRepository.deleteById(id)
    }
}
