package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.domain.Section.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SectionTest {

    @Test
    @DisplayName("상행역과 하행역이 같은 구간은 생성할 수 없다.")
    void constructThrowException1() {
        assertThatThrownBy(() ->
                new Section(1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_STATION_EXCEPTION_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,-1})
    @DisplayName("거리가 0 이하인 구간은 생성할 수 없다.")
    void constructThrowException2(int distance) {
        assertThatThrownBy(() ->
                new Section(1L, 1L, 2L, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(STATION_DISTANCE_EXCEPTION_MESSAGE);
    }
}
