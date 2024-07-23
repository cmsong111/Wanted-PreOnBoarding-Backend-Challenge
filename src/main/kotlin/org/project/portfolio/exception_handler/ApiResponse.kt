package org.project.portfolio.exception_handler

class ApiResponse(
    val resultCode: String,
    val resultMessage: String
) {
    override fun toString(): String {
        return "ApiResponse(resultCode='$resultCode', resultMessage='$resultMessage')"
    }
}
