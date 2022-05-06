package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("생성 시 distance 가 0 이하인 경우 예외 발생")
    void createExceptionByNotPositiveDistance(final int distance) {
        assertThatThrownBy(() -> new Section(1L, 1L, 2L, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 길이는 양수만 들어올 수 있습니다.");

    }
}
