package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {
    @DisplayName("ID가 같으면 같은 노선으로 취급한다.")
    @Test
    void equals() {
        assertThat(new Line(1L, "1호선", "bg-red-600"))
            .isEqualTo(new Line(1L, "2호선", "bg-blue-600"));
    }

    @DisplayName("ID가 다르면 다른 역으로 취급한다.")
    @Test
    void notEquals() {
        assertThat(new Line(1L, "1호선", "bg-red-600"))
            .isNotEqualTo(new Line(2L, "1호선", "bg-red-600"));
    }
}
