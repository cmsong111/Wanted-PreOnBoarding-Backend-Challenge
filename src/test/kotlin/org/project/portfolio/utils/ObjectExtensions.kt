package org.project.portfolio.utils

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.prettyJson(): String {
    return jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(this)
}
