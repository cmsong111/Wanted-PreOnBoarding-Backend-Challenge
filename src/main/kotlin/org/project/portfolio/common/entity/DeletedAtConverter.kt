package org.project.portfolio.common.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Instant

/**
 * 삭제 여부 컨버터
 * 삭제 여부를 표현할 때 AttributeConverter에는 NULL값이 들어갈 수 없기 때문에 다음과 같이 처리한다.
 * <ol>
 * <li>true: 삭제된 시간</li>
 * <li>false: 1970-01-01 09:00:00 (GMT+9)(Timestamp(0))</li>
 * </ol>
 */
@Converter
class DeletedAtConverter : AttributeConverter<Boolean, Instant> {
    /**
     * 삭제 여부를 데이터베이스에 저장할 때 사용
     * @param attribute 삭제 여부
     */
    override fun convertToDatabaseColumn(attribute: Boolean): Instant {
        return if (attribute) {
            Instant.now()
        } else {
            Instant.ofEpochMilli(0)
        }
    }

    /**
     * 삭제 여부를 판단하는 메소드
     */
    override fun convertToEntityAttribute(dbData: Instant): Boolean {
        return dbData.toEpochMilli() != 0L
    }
}
