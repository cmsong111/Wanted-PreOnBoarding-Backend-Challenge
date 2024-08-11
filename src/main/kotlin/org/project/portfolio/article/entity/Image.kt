package org.project.portfolio.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.project.portfolio.common.BaseEntity

@Entity
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var url: String,
    @ManyToOne
    var article: Article
) : BaseEntity() {
}
