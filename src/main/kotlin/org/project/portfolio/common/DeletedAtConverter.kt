package org.project.portfolio.common

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.sql.Timestamp

/**
 * 삭제 여부 컨버터
 * 삭제 여부를 표현할 때 AttributeConverter에는 NULL값이 들어갈 수 없기 때문에 다음과 같이 처리한다.
 * <ol>
 * <li>true: 삭제된 시간</li>
 * <li>false: 1970-01-01 09:00:00 (GMT+9)(Timestamp(0))</li>
 * </ol>
 */
@Converter
class DeletedAtConverter : AttributeConverter<Boolean, Timestamp> {

    /**
     * 삭제 여부를 데이터베이스에 저장할 때 사용
     * @param attribute 삭제 여부
     */
    override fun convertToDatabaseColumn(attribute: Boolean): Timestamp {
        return if (attribute) {
            Timestamp(System.currentTimeMillis())
        } else {
            Timestamp(0)
        }
    }

    /**
     * 삭제 여부를 판단하는 메소드
     */
    override fun convertToEntityAttribute(dbData: Timestamp): Boolean {
        return dbData.time != 0L
    }
}
