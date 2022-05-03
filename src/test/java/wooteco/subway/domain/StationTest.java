package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @ParameterizedTest
    @DisplayName("역 이름이 공백이면 예외가 발생한다")
    @ValueSource(strings = {"", " ", "    "})
    void save_blankName(String name) {
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역의 이름이 공백이 되어서는 안됩니다.");
    }
}
