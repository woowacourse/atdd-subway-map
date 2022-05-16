package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.line.Line;

class LineTest {

    @Test
    @DisplayName("이름이 같은지 확인한다.")
    public void hasSameNameWith() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        // when
        final boolean hasSameName = line.hasSameNameWith(new Line("신분당선", "bg-red-600"));
        // then
        assertThat(hasSameName).isTrue();
    }
}
