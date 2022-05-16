package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.DataLengthException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    @DisplayName("역의 이름이 빈 값이거나 최대범위를 초과한 경우 예외를 발생한다.")
    @ParameterizedTest(name = "{index} {displayName} name: {0}")
    @ValueSource(strings = {"", "   ", "aaaaaaaaaaaaaaaaaaaaa"})
    void throwsExceptionWithInvalidNameLength(final String name) {

        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(DataLengthException.class);
    }
}
