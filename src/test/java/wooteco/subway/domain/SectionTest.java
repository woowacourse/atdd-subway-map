package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("같은 역간의 구간 생성 시 예외가 발생한다.")
    @Test
    void constructor_SameStation_ThrowsException() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("같은 역간의 구간 생성 시 예외가 발생한다.")
    @Test
    void constructor_InvalidDistance_ThrowsException() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 2L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
