package org.project.portfolio.notification


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.notification.entity.Notification
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.security.Principal

@Tag(name = "5. Notification", description = "The notification API")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val notificationService: NotificationService
) {

    /** 현재 연결된 모든 SSE 클라이언트 */
    private val emitters: MutableList<SseEmitter> = mutableListOf()

    @Operation(summary = "알림 구독 API", description = "Client 측에서 SSE 신호를 처리하기 위해 Event-Source-Polyfill와 같은 라이브러리를 사용해야 함")
    @GetMapping(path = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun notificationSubscribe(
        principal: Principal
    ): SseEmitter {
        return notificationService.notificationSubscribe(principal.name)
    }

    @Operation(summary = "알림 내역 조회 API")
    @GetMapping
    fun getNotifications(
        principal: Principal
    ): ResponseEntity<List<Notification>> {
        return ResponseEntity.ok(notificationService.getNotifications(principal.name))
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "알림 발송 API(관리자용)")
    fun sendNotification(
        @RequestParam @Parameter(description = "알림 메시지 제목") title: String,
        @RequestParam @Parameter(description = "알림 메시지 내용") content: String,
        @RequestParam(required = false) @Parameter(description = "알람을 전달 받을 사람<br>Null인 경우 전체 알림") email: String?,
        principal: Principal
    ): ResponseEntity<String> {
        notificationService.sendNotification(
            email = email,
            title = title,
            content = content,
            sender = principal.name
        )
        return ResponseEntity.accepted().body("OK")
    }
}
