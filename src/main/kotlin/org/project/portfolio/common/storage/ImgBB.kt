package org.project.portfolio.common.storage

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Component
@Profile("prod", "local")
class ImgBB(
    @Value("\${storage.imgbb.api-key}")
    private val apiKey: String,
) : StorageService {
    private val factory = HttpServiceProxyFactory.builder()
        .exchangeAdapter(RestClientAdapter.create(RestClient.builder().build()))
        .build()

    private val imgbbClient = factory.createClient(ImgBBClient::class.java)

    override fun uploadFile(file: MultipartFile): String {
        imgbbClient
            .uploadImage(
                key = apiKey,
                name = file.originalFilename,
                image = file.bytes,
            )
            .let { response ->
                return response["data"]["url"].asText()
            }
    }

    @HttpExchange(url = "https://api.imgbb.com/1")
    interface ImgBBClient {
        @PostMapping("/upload", produces = [MediaType.MULTIPART_FORM_DATA_VALUE])
        fun uploadImage(
            @RequestParam key: String,
            @RequestParam name: String? = null,
            @RequestParam expiration: Int? = null,
            @RequestPart image: ByteArray,
        ): JsonNode
    }
}
