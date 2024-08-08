package org.project.portfolio.common

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("DeletedAtConverter 단위 테스트")
class DeletedAtConverterTest {

    @Test
    @DisplayName("삭제 안된 경우")
    fun convertToEntityAttribute() {
        // given
        val converter = DeletedAtConverter()

        // when
        val result = converter.convertToEntityAttribute(Timestamp(0))

        // then
        assertFalse { result }
    }

    @Test
    @DisplayName("삭제된 경우")
    fun convertToEntityAttribute2() {
        // given
        val converter = DeletedAtConverter()

        // when
        val result = converter.convertToEntityAttribute(Timestamp(1000))

        // then
        assertTrue { result }
    }
}
