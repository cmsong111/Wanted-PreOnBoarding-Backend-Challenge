package org.project.portfolio.notification.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.project.portfolio.notification.dto.NotificationRequestDto
import org.project.portfolio.notification.entity.Notification
import org.project.portfolio.notification.repository.NotificationRepository
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    /** 현재 연결된 모든 SSE 클라이언트 */
    private val emitters: HashMap<String, SseEmitter> = hashMapOf()

    private val objectMapper = ObjectMapper()

    /** 알림 구독 API */
    fun notificationSubscribe(email: String): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        emitter.send(SseEmitter.event().name("system").data("connected"))
        // 최근 10개의 알림을 전송
        notificationRepository.findByReceiver_EmailOrderByCreatedAtDesc(email, PageRequest.of(0, 10)).forEach {
            emitter.send(SseEmitter.event().name("notification").data(objectMapper.writeValueAsString(it)))
        }
        emitters[email] = emitter
        return emitter
    }

    /** 알림 발송 API */
    fun sendNotification(notificationRequestDto: NotificationRequestDto, sender: String) {
        // 전체 알림인 경우
        if (notificationRequestDto.receiver == null) {
            // 알림 저장
            userRepository.findAll().forEach {
                val notification = saveNotification(notificationRequestDto, receiver = it, sender = sender)
                emitters[notification.receiver.email]?.send(
                    SseEmitter.event().name("notification").data(objectMapper.writeValueAsString(notification))
                )
            }
        }
        // 특정 사용자에게 알림을 보내는 경우
        else {
            userRepository.findById(notificationRequestDto.receiver).orElseThrow().let {
                val notification = saveNotification(notificationRequestDto, receiver = it, sender = sender)
                emitters[it.email]?.send(SseEmitter.event().name("notification").data(objectMapper.writeValueAsString(notification)))
            }
        }
    }

    private fun saveNotification(notificationRequestDto: NotificationRequestDto, receiver: User, sender: String): Notification {
        val notification: Notification = Notification(
            title = notificationRequestDto.title!!,
            content = notificationRequestDto.content!!,
            receiver = receiver,
            sender = notificationRequestDto.sender ?: sender
        )
        return notificationRepository.save(notification)
    }

    /**
     * 유저 별 알림 내역 조회
     * @param email 사용자 이메일
     */
    fun getNotifications(email: String): List<Notification> {
        return notificationRepository.findByReceiver_EmailOrderByCreatedAtDesc(email)
    }


}
