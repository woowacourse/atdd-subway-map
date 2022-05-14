package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.DataLengthException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @DisplayName("노선의 이름이 빈 값이거나 최대범위인 20을 초과한 경우 예외를 발생한다.")
    @ParameterizedTest(name = "{index} {displayName} name: {0}")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaa"})
    void throwsExceptionWithInvalidNameLength(final String name) {

        assertThatThrownBy(() -> new Line(name, "red"))
                .isInstanceOf(DataLengthException.class);
    }

    @DisplayName("노선의 색이 빈 값이거나 최대범위인 20을 초과한 경우 예외를 발생한다.")
    @ParameterizedTest(name = "{index} {displayName} color: {0}")
    @ValueSource(strings = {"", "yellowYellowYellowYellowYellow"})
    void throwsExceptionWithInvalidColorLength(final String color) {

        assertThatThrownBy(() -> new Line("신분당선", color))
                .isInstanceOf(DataLengthException.class);
    }
}
