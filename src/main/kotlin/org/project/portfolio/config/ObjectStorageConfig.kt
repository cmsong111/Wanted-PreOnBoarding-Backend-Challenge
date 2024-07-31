package org.project.portfolio.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObjectStorageConfig(
    @Value("\${minio.url}")
    val awsEndpoint: String,
    @Value("\${cloud.aws.credentials.access-key}")
    val awsAccessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}")
    val awsSecretKey: String,
    @Value("\${cloud.aws.region.static}")
    val awsRegion: String
) {

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3Client.builder()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKey, awsSecretKey)))
            .build()
    }
}
