package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.domain.StationException;

class StationTest {

    @ParameterizedTest
    @DisplayName("역 이름이 공백이면 예외가 발생한다")
    @ValueSource(strings = {"", " ", "    "})
    void newStation_blankName(String name) {
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(StationException.class)
                .hasMessage("역의 이름이 공백이 되어서는 안됩니다.");
    }

    @Test
    @DisplayName("역 이름이 허용된 길이를 넘으면 예외가 발생한다.")
    void newStation_nameLength() {
        // given
        final String name = "1234567890123456";

        // then
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(StationException.class)
                .hasMessage("역의 이름이 15자를 넘어서는 안됩니다.");

    }
}
