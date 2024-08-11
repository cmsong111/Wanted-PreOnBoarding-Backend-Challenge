package org.project.portfolio.storage

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.Base64


@Component
class Imagebb(
    @Value("\${imagebb.api-key}") private val imagebbApiKey: String
) : StorageService {

    private val logger = LoggerFactory.getLogger(Imagebb::class.java)
    private val restTemplate = RestTemplate()
    private val uploadUrl = "https://api.imgbb.com"

    override fun uploadFile(file: MultipartFile): String {
        // URI 구성
        val uri: URI = UriComponentsBuilder
            .fromUriString(uploadUrl)
            .path("/1/upload")
            .queryParam("key", imagebbApiKey)
            .queryParam("expiration", 15552000)
            .encode()
            .build()
            .toUri()

        // 요청 파라미터 설정
        val body = LinkedMultiValueMap<String, Any>().apply {
            // image <- Base64 인코딩된 파일 데이터
            add("image", Base64.getEncoder().encodeToString(file.bytes))
        }

        // 헤더 설정
        val headers = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
        }

        // HttpEntity 생성
        val requestEntity = HttpEntity(body, headers)

        // 요청 보내기
        val response = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )

        // 응답 처리
        if (response.statusCode.is2xxSuccessful) {
            // 응답 JSON을 파싱하여 이미지 URL 추출 (예시)
            val responseBody = response.body ?: throw RuntimeException("Response body is null")
            val jsonResponse: ImagebbDto = ObjectMapper().readValue(responseBody, ImagebbDto::class.java)

            return jsonResponse.data?.url ?: throw RuntimeException("Failed to get image URL from Imagebb response")
        } else {
            throw RuntimeException("Failed to upload image to Imagebb: ${response.statusCode}")
        }
    }
}
