package org.project.portfolio.storage

import org.springframework.web.multipart.MultipartFile

interface StorageService {
    fun uploadFile(file: MultipartFile): String
}
