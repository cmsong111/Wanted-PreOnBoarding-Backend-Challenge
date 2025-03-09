package org.project.portfolio.common.exception.dto

data class ApiResponse(
    val resultCode: String,
    val resultMessage: String
) {
    override fun toString(): String {
        return "ApiResponse(resultCode='$resultCode', resultMessage='$resultMessage')"
    }
}
