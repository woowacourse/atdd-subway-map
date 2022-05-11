package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private final Section section = new Section(1L, 1L, 2L, 5);

    @Test
    @DisplayName("상행과 하행이 같은 역이면 예외가 발생한다.")
    void validateStationId() {
        long upStationId = 1L;
        long downStationId = 1L;
        Assertions.assertThatThrownBy(() -> new Section(1L, upStationId, downStationId, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행과 하행은 서로 다른 역이어야 합니다.");
    }
}
