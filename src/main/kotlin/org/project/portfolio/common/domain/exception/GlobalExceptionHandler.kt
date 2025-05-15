package org.project.portfolio.common.domain.exception

import org.project.portfolio.common.presentation.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
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
                    code = conflictException.message,
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
}
