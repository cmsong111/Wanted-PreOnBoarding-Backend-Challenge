package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.ArticleView
import org.springframework.data.keyvalue.repository.KeyValueRepository
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@EnableRedisRepositories
interface ArticleRedisRepository : KeyValueRepository<ArticleView, String>
