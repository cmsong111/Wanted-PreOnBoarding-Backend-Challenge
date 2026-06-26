package org.project.portfolio.common.presentation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.common.domain.exception.ConflictException
import org.project.portfolio.common.domain.exception.ForbiddenException
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.project.portfolio.common.presentation.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/** RestAPI 전역 예외 처리 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    private fun handleBadRequestException(badRequestException: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    code = badRequestException.code,
                    message = badRequestException.message,
                    properties = badRequestException.properties,
                ),
            )
    }

    @ExceptionHandler(UnauthorizedException::class)
    private fun handleUnauthorizedException(unauthorizedException: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponse(
                    code = unauthorizedException.code,
                    message = unauthorizedException.message,
                    properties = unauthorizedException.properties,
                ),
            )
    }

    @ExceptionHandler(ForbiddenException::class)
    private fun handleForbiddenException(forbiddenException: ForbiddenException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    code = forbiddenException.code,
                    message = forbiddenException.message,
                    properties = forbiddenException.properties,
                ),
            )
    }

    @ExceptionHandler(AccessDeniedException::class)
    private fun handleAccessDeniedException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    code = "FORBIDDEN",
                    message = "접근 권한이 없습니다.",
                ),
            )
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private fun handleNotFoundException(notFoundException: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    code = notFoundException.code,
                    message = notFoundException.message,
                    properties = notFoundException.properties,
                ),
            )
    }

    @ExceptionHandler(ConflictException::class)
    private fun handleConflictException(conflictException: ConflictException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    code = conflictException.code,
                    message = conflictException.message,
                    properties = conflictException.properties,
                ),
            )
    }

    /** 입력값 오류 처리 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
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
                ),
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class, MissingRequestHeaderException::class)
    private fun handleBadRequestFormat(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    code = "BAD_REQUEST",
                    message = e.message ?: "잘못된 요청입니다.",
                ),
            )
    }

    @ExceptionHandler(Exception::class)
    private fun handleUnexpectedException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error(e) { "Unhandled exception occurred" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    code = "INTERNAL_SERVER_ERROR",
                    message = "서버 내부 오류가 발생했습니다.",
                ),
            )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
