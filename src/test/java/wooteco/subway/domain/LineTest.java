package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    @Test
    @DisplayName("id가 동일하다면 true 반환")
    void isSameId() {
        assertThat(new Line(1L, "name", "red").isSameId(1L)).isTrue();
    }
}