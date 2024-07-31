package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {


    fun findByCreatedAtBetween(createdAtStart: Timestamp, createdAtEnd: Timestamp): List<Article>
}
