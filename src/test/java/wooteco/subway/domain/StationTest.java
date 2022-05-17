package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @DisplayName("역의 이름은 255자를 초과할 수 없다.")
    @Test
    void validateNameLength() {
        assertThatThrownBy(() -> new Station("a".repeat(256)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 이름은 255자를 초과하면 안됩니다.");
    }

    @DisplayName("역의 이름은 빈 문자열일 수 없다.")
    @ParameterizedTest(name = "input =  [" + ARGUMENTS_PLACEHOLDER + "]")
    @ValueSource(strings = {"  ", ""})
    void validateBlankName(String input) {
        assertThatThrownBy(() -> new Station(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 이름은 빈 문자열일 수 없습니다.");
    }

    @DisplayName("역의 이름은 null일 수 없다.")
    @Test
    void validateNull() {
        assertThatThrownBy(() -> new Station(null))
                .isInstanceOf(NullPointerException.class);
    }
}
