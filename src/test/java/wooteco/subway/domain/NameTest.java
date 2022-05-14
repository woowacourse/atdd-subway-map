package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.property.Name;
import wooteco.subway.exception.InvalidRequestException;

class NameTest {

    @Test
    @DisplayName("null로 생성할 수 없다.")
    public void throwsExceptionWithNull() {
        // given & when
        String value = null;
        // then
        assertThatExceptionOfType(InvalidRequestException.class)
            .isThrownBy(() -> new Name(value));
    }

    @Test
    @DisplayName("빈문자열로 생성할 수 없다.")
    public void throwsExceptionWithBlank() {
        // given & when
        String value = "";
        // then
        assertThatExceptionOfType(InvalidRequestException.class)
            .isThrownBy(() -> new Name(value));
    }
}