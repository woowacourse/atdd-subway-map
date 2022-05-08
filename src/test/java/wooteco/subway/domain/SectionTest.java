package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행과 하행의 id값이 같은 경우 에러를 발생시킨다")
    @Test
    void validateErrorBySameStationId() {
        assertThatThrownBy(() -> Section.of(1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행과 하행의 지하철 역이 같을 수 없습니다.");
    }

    @DisplayName("거리가 0이하인 경우 에러를 발생시킨다.")
    @Test
    void validateErrorByNonPositiveDistance() {
        assertThatThrownBy(() -> Section.of(1L, 1L, 2L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 양수여야 합니다.");
    }
}