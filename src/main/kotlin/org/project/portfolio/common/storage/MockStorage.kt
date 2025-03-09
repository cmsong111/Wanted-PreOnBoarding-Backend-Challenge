package org.project.portfolio.common.storage

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
@Profile("test")
class MockStorage : StorageService {
    override fun uploadFile(file: MultipartFile): String {
        return "https://mock-storage.com/${file.originalFilename}"
    }
}
