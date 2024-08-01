package org.project.portfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration


@EnableCaching
@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") val redisHost: String,
    @Value("\${spring.data.redis.port}") val redisPort: Int
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort)
    }

    @Bean
    fun cacheManager(): RedisCacheManager {
        return RedisCacheManager.builder(redisConnectionFactory()).cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    // Set cache expiration to 10 seconds
                    .entryTtl(Duration.ofSeconds(60))
                    // Serialize values with GenericJackson2JsonRedisSerializer
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                            GenericJackson2JsonRedisSerializer()
                        )
                    )
            ).build()
    }
}
