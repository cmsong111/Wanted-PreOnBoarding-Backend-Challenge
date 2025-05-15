package org.project.portfolio.article.domain

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.repository.CrudRepository

@EnableRedisRepositories
interface ArticleViewRepository : CrudRepository<ArticleView, String>
