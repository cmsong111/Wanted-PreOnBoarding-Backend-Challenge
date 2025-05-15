package org.project.portfolio.common.domain.exception

sealed class CustomException(
    val code: String,
    override val message: String,
    val properties: Map<String, Any?> = mapOf(),
) : RuntimeException(message)

class BadRequestException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("BAD_REQUEST", message, properties)

class UnauthorizedException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("UNAUTHORIZED", message, properties)

class ForbiddenException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("FORBIDDEN", message, properties)

class NotFoundException(
    clazz: Class<*>,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("${clazz.simpleName.uppercase()}_NOT_FOUND", "${clazz.simpleName} not found", properties)

class ConflictException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("CONFLICT", message, properties)
