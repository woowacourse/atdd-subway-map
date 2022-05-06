package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @ParameterizedTest
    @DisplayName("이름이 공백이면 예외가 발생한다")
    @ValueSource(strings = {"", " ", "    "})
    void NewStation_BlankName_ExceptionThrown(final String value) {
        assertThatThrownBy(() -> new Station(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름이 공백이 되어서는 안됩니다.");
    }

    @Test
    @DisplayName("이름이 허용된 길이를 넘으면 예외가 발생한다.")
    void NewStation_NameLength_ExceptionThrown() {
        // given
        final String name = "1234567890123456";

        // then
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름이 15자를 넘어서는 안됩니다.");
    }
}