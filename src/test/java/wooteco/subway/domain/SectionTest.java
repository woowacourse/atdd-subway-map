package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("상행 종점과 하행 종점이 다르면 예외가 발생한다")
    @Test
    void sameUpStationAndDownStation() {
        Assertions.assertThatThrownBy(() -> Section.of(1L, 1L, 1L, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 양의 정수가 아니면 예외가 발생한다")
    @Test
    void unvalidDistanceValue() {
        Assertions.assertThatThrownBy(() -> Section.of(1L, 1L, 2L, -3))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
