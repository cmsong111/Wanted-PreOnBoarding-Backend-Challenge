package org.project.portfolio.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.prettyJson(): String {
    return jacksonObjectMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(this)
}
