package org.project.portfolio.article.domain

import org.springframework.data.keyvalue.repository.KeyValueRepository
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@EnableRedisRepositories
interface ArticleViewRepository : KeyValueRepository<ArticleView, String>
