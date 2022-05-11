package wooteco.subway.domain;

//        if (lineRequest.getUpStationId() == lineRequest.getDownStationId()) {
//                throw new BadRequestLineException("상행선과 하행선은 같은 지하철 역이면 안됩니다.");
//                }
//
//                if (lineRequest.getDistance() < 1) {
//        throw new BadRequestLineException("상행선과 하행선의 거리는 1 이상이어야 합니다.");
//        }

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 관련 기능")
public class SectionTest {

    @DisplayName("상행선과 하행선은 같은 역으로 할 수 없다.")
    @Test
    void duplicateStationException() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에서 상행선과 하행선은 같은 역으로 할 수 없습니다.");
    }

    @DisplayName("상행선과 하행선의 거리는 1 이상이어야 한다.")
    @Test
    void distanceException() {
        assertThatThrownBy(() -> new Section(1L, 2L, 1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행선과 하행선의 거리는 1 이상이어야 합니다.");
    }
}
