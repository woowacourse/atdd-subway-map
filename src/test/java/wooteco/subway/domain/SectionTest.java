package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.section.SectionRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @Test
    @DisplayName("상행종점과 하행종점이 동일한 경우")
    void sameUpDown() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 1L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행종점과 하행종점은 같은 지하철역일 수 없습니다.");
    }

    @Test
    @DisplayName("거리가 1미만의 정수인 경우")
    void wrongDistance() {
        assertThatThrownBy(() -> Section.of(1L, new SectionRequest(1L, 2L, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 1이상의 정수만 허용됩니다.");
    }
}
