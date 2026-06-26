package org.project.portfolio.notification.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.project.portfolio.config.SwaggerConfig.Companion.NOTIFICATIONS_API_TAG
import org.project.portfolio.notification.application.NotificationCommandService
import org.project.portfolio.notification.application.NotificationQueryService
import org.project.portfolio.notification.application.NotificationSseService
import org.project.portfolio.notification.presentation.request.NotificationRequestDto
import org.project.portfolio.notification.presentation.response.NotificationResponse
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Tag(name = NOTIFICATIONS_API_TAG, description = "The notification API")
@RestController
@SecurityRequirement(name = BEARER_AUTH)
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationQueryService: NotificationQueryService,
    private val notificationCommandService: NotificationCommandService,
    private val notificationSseService: NotificationSseService,
) {
    @Operation(summary = "알림 구독 API", description = "Client 측에서 SSE 신호를 처리하기 위해 Event-Source-Polyfill와 같은 라이브러리를 사용해야 함")
    @GetMapping(path = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @PreAuthorize("isAuthenticated()")
    fun notificationSubscribe(
        @AuthenticationPrincipal jwt: Jwt,
    ): SseEmitter {
        return notificationSseService.subscribe(jwt.subject)
    }

    @Operation(summary = "알림 내역 조회 API")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getNotifications(
        @AuthenticationPrincipal jwt: Jwt,
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PagedModel<NotificationResponse>> {
        return ResponseEntity.ok(
            PagedModel(
                notificationQueryService.getNotifications(
                    email = jwt.subject,
                    pageable = pageable,
                ),
            ),
        )
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "알림 발송 API(관리자용)")
    fun sendNotification(
        @RequestBody @Valid notificationRequestDto: NotificationRequestDto,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        notificationCommandService.sendNotification(
            notificationRequestDto,
            jwt.subject,
        )
        return ResponseEntity.accepted().body("OK")
    }
}
