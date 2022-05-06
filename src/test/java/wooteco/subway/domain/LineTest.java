package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("노선 객체 생성에 성공한다.")
    void NewLine() {
        // when
        final Line line = new Line("7호선", "bg-red-600");

        // then
        assertThat(line).isNotNull();
    }
}
