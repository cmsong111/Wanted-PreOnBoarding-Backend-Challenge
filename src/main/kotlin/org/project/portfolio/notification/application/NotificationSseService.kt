package org.project.portfolio.notification.application

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.notification.presentation.response.NotificationResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class NotificationSseService(
    private val notificationQueryService: NotificationQueryService,
    private val objectMapper: ObjectMapper,
) {
    /** 현재 연결된 모든 SSE 클라이언트 (이메일별 다중 연결 지원) */
    private val emitters = ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>>()

    /** 알림 구독 */
    fun subscribe(email: String): SseEmitter {
        val emitter = SseEmitter(SSE_TIMEOUT_MS)
        val userEmitters = emitters.computeIfAbsent(email) { CopyOnWriteArrayList() }
        userEmitters.add(emitter)

        val cleanup = { removeEmitter(email, emitter) }
        emitter.onCompletion(cleanup)
        emitter.onTimeout(cleanup)
        emitter.onError { cleanup() }

        sendEvent(email, emitter, "system", "connected")

        // 최근 10개의 알림을 초기 전송
        notificationQueryService.getNotifications(email, PageRequest.of(0, 10)).forEach {
            sendEvent(email, emitter, "notification", objectMapper.writeValueAsString(it))
        }

        return emitter
    }

    /** 특정 사용자에게 실시간 SSE 알림 전송 */
    fun sendToUser(
        email: String,
        notification: NotificationResponse,
    ) {
        val userEmitters = emitters[email] ?: return
        val payload = objectMapper.writeValueAsString(notification)
        userEmitters.forEach { emitter ->
            sendEvent(email, emitter, "notification", payload)
        }
    }

    private fun sendEvent(
        email: String,
        emitter: SseEmitter,
        eventName: String,
        data: Any,
    ) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (e: IOException) {
            logger.debug(e) { "SSE 전송 실패로 연결 제거: email=$email" }
            removeEmitter(email, emitter)
        }
    }

    private fun removeEmitter(
        email: String,
        emitter: SseEmitter,
    ) {
        emitters.computeIfPresent(email) { _, list ->
            list.remove(emitter)
            if (list.isEmpty()) null else list
        }
    }

    companion object {
        private const val SSE_TIMEOUT_MS = 60L * 60L * 1000L // 1시간
        private val logger = KotlinLogging.logger {}
    }
}
