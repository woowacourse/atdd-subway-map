package wooteco.subway.line;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @Test
    void lineCreateTest() {
        Line line = new Line("2호선", "color name");

        assertThat(line).isInstanceOf(Line.class);
    }
}
