package org.project.portfolio.common.storage

import org.springframework.web.multipart.MultipartFile

interface StorageService {
    fun uploadFile(file: MultipartFile): String
}
