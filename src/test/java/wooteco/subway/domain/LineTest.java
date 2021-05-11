package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @Test
    @DisplayName("노선 객체 생성 테스트")
    void lineCreateTest() {
        Line line = new Line("2호선", "color name");

        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("color name");
    }
}
