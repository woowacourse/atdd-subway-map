package wooteco.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LineTest {
    @Test
    void checkNullOrEmpty() {
        assertThatThrownBy(() -> new Line(1L, null, "red"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Line(1L, "", "red"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Line(1L, "leo", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Line(1L, "leo", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}