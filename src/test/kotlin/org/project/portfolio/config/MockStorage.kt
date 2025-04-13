package org.project.portfolio.config

import org.project.portfolio.common.storage.StorageService
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class MockStorage : StorageService {
    override fun uploadFile(file: MultipartFile): String {
        return "https://mock-storage.com/${file.originalFilename}"
    }
}
