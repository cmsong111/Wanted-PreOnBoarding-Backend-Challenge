package org.project.portfolio.config

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


@Configuration
class MinioClientConfig(
    @Value("\${minio.url}")
    val minIoUrl: String,
    @Value("\${minio.access-key}")
    val minIoAccessKey: String,
    @Value("\${minio.secret-key}")
    val minIoSecretKey: String
) {
    /** Minio Client */
    final var minioClient: MinioClient = MinioClient.builder()
        .endpoint(minIoUrl)
        .credentials(minIoAccessKey, minIoSecretKey)
        .build()

    /** Create bucket if not exists */
    init {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("article").build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("article").build());
        }
    }
}
