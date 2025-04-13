package org.project.portfolio.common.domain.exception

import org.project.portfolio.common.presentation.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/** RestAPI 전역 예외 처리 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleBadRequestException(badRequestException: BadRequestException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = badRequestException.message,
            message = badRequestException.message,
            properties = badRequestException.properties,
        )
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private fun handleUnauthorizedException(unauthorizedException: UnauthorizedException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = unauthorizedException.message,
            message = unauthorizedException.message,
            properties = unauthorizedException.properties,
        )
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private fun handleForbiddenException(forbiddenException: ForbiddenException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = forbiddenException.message,
            message = forbiddenException.message,
            properties = forbiddenException.properties,
        )
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private fun handleNotFoundException(notFoundException: NotFoundException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = notFoundException.message,
            message = notFoundException.message,
            properties = notFoundException.properties,
        )
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private fun handleConflictException(conflictException: ConflictException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = conflictException.message,
            message = conflictException.message,
            properties = conflictException.properties,
        )
    }

    /** 입력값 오류 처리 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ApiResponse<Unit> {
        return ApiResponse.fail(
            code = "INPUT_VALUE_INVALID",
            message = "입력값이 올바르지 않습니다.",
            properties =
                mapOf(
                    "fieldErrors" to e.fieldErrors.map { fieldError ->
                        mapOf(
                            "field" to fieldError.field,
                            "code" to fieldError.code,
                            "rejectedValue" to fieldError.rejectedValue,
                            "message" to fieldError.defaultMessage,
                        )
                    },
                    "globalErrors" to e.globalErrors.map { globalError ->
                        mapOf(
                            "code" to globalError.code,
                            "message" to globalError.defaultMessage,
                        )
                    },
                ),
        )
    }
}
