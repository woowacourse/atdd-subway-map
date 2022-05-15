package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @DisplayName("거리가 0이하인 경우, IllegalArgumentException이 발생")
    @ValueSource(ints = {0, -1, -100})
    @ParameterizedTest
    void constructor_throwsExceptionWithValueNotGreaterThanZero(int input) {
        assertThatThrownBy(() -> new Distance(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0이하일 수 없습니다.");
    }
}
