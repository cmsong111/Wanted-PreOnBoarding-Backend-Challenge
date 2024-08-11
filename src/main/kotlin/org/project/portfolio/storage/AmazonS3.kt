package org.project.portfolio.storage

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile


@Component
class AmazonS3(
    @Value("\${minio.url}")
    val awsEndpoint: String,
    @Value("\${cloud.aws.credentials.access-key}")
    val awsAccessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}")
    val awsSecretKey: String,
    @Value("\${cloud.aws.region.static}")
    val awsRegion: String,
    @Value("\${cloud.aws.s3.bucket}")
    val awsBucket: String
) : StorageService {


    val amazonS3: AmazonS3 = AmazonS3Client.builder()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
        .withPathStyleAccessEnabled(true)
        .withClientConfiguration(ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKey, awsSecretKey)))
        .build()

    override fun uploadFile(file: MultipartFile): String {
        val key = "${System.currentTimeMillis()}_${file.originalFilename}"
        val metadata = ObjectMetadata().apply {
            contentType = file.contentType
            contentLength = file.size
        }
        amazonS3.putObject(awsBucket, key, file.inputStream, metadata)
        return amazonS3.getUrl(awsBucket, key).toString()
    }

}
