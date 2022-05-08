package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("동일한 이름을 갖는다.")
    @Test
    void isSameName() {
        Line line = new Line("2호선", "red");
        assertThat(line.isSameName("2호선")).isTrue();
    }
}
