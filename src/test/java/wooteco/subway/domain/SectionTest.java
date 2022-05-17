package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @DisplayName("상행역, 하행역이 같은 역이면 예외가 발생한다.")
    @Test
    void throwsExceptionWithSameStation() {
        Long lineId = 1L;
        Long stationId = 2L;
        int distance = 10;
        assertThatThrownBy(() -> new Section(lineId, stationId, stationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("상행역, 하행역은 다른 역이어야 합니다.");
    }

    @DisplayName("구간 사이의 거리가 0보다 작으면 예외가 발생한다.")
    @Test
    void throwsExceptionWithInvalidDistance() {
        Long lineId = 1L;
        Long upStationId = 2L;
        Long downStationId = 3L;
        int distance = 0;
        assertThatThrownBy(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("구간 사이의 거리는 0보다 커야합니다.");
    }
}
