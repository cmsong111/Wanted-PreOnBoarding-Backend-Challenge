package org.project.portfolio.exception_handler

/** 비즈니스 예외 */
class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message) {
}
