package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("구간 거리가 0 이하인 경우, IllegalArgumentException이 발생한다.")
    void section_distance_zero() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 2L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
