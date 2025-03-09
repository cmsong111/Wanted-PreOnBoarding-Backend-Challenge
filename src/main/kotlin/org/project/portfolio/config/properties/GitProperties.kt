package org.project.portfolio.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "git")
data class GitProperties
    @ConstructorBinding
    constructor(
        val branch: String,
        val build: Build,
        val commit: Commit,
        val remote: Remote,
    ) {
        data class Build(
            val host: String,
            val user: User,
            val version: String,
        )

        data class Commit(
            val id: String,
            val message: Message,
            val time: String,
            val user: User,
        )

        data class Message(
            val full: String,
            val short: String,
        )

        data class User(
            val name: String,
            val email: String,
        )

        data class Remote(
            val origin: Origin,
        )

        data class Origin(
            val url: String,
        )
    }
