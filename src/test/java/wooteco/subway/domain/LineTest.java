package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("Line 생성 테스트")
    void create_Line() {
        Line line = new Line(1L, "name", "color");

        assertThat(line).isNotNull();
    }
}
