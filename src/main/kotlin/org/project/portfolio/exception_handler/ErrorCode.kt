package org.project.portfolio.exception_handler

import org.springframework.http.HttpStatus

/** 에러 코드 */
enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) {
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "C002", "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "C003", "토큰을 찾을 수 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "유효하지 않은 사용자입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 사용자입니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "유효하지 않은 비밀번호입니다."),
    USER_INVALID_EMAIL(HttpStatus.BAD_REQUEST, "U004", "유효하지 않은 이메일입니다."),
    USER_INVALID_PHONE(HttpStatus.BAD_REQUEST, "U005", "유효하지 않은 전화번호입니다."),
    USER_INVALID_NAME(HttpStatus.BAD_REQUEST, "U006", "유효하지 않은 이름입니다."),

    INVALID_ACCESS(HttpStatus.FORBIDDEN, "A001", "접근 권한이 없습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "V001", "입력값이 올바르지 않습니다."),

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "게시글을 찾을 수 없습니다."),
    ARTICLE_AUTHOR_NOT_MATCH(HttpStatus.FORBIDDEN, "A002", "게시글 작성자와 요청자가 일치하지 않습니다."),
    ;
}
