package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LinesTest {
    private final Lines lines = new Lines(
            Arrays.asList(
                    new Line(1L, "red", "hi"),
                    new Line(2L, "green", "hello"),
                    new Line(3L, "black", "im"),
                    new Line(4L, "white", "sakjung")
            )
    );

    @DisplayName("line중에 같은 이름이 있으면 true, 없으면 false를 반환한다")
    @Test
    void doesNameExist() {
        assertThat(lines.doesNameExist("hello")).isTrue();
        assertThat(lines.doesNameExist("zino")).isFalse();
    }

    @DisplayName("line중에 같은 id가 없으면 true, 있으면 false를 반환한다")
    @Test
    void doesIdNotExist() {
        assertThat(lines.doesIdNotExist(1L)).isFalse();
        assertThat(lines.doesIdNotExist(5L)).isTrue();
    }
}