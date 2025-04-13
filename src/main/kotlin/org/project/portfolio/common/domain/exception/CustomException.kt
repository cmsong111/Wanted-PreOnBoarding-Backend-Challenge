package org.project.portfolio.common.domain.exception

abstract class CustomException(
    val code: String,
    override val message: String,
    val properties: Map<String, Any?> = mapOf(),
) : RuntimeException(message)

abstract class BadRequestException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("BAD_REQUEST", message, properties)

abstract class UnauthorizedException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("UNAUTHORIZED", message, properties)

abstract class ForbiddenException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("FORBIDDEN", message, properties)

abstract class NotFoundException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("NOT_FOUND", message, properties)

abstract class ConflictException(
    message: String,
    properties: Map<String, Any?> = mapOf(),
) : CustomException("CONFLICT", message, properties)
