package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionException;

class SectionTest {

    @DisplayName("두 종점간의 거리는 0보다 작거나 같을 수 없다.")
    @Test
    public void negativeDistance() {
        assertThatThrownBy(() -> new Section(1L, 1L, 2L, 0))
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행 종점과 하행 종점은 같은 역일 수 없다.")
    @Test
    public void sameUpAndDownStation() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 1))
                .isInstanceOf(IllegalSectionException.class);
    }
}
