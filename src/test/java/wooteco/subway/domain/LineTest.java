package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    @Test
    @DisplayName("isSameName 메서드는 이름이 같은지 확인한다")
    void isSameName() {
        Line line = new Line("test", "BLUE");

        assertThat(line.isSameName("test")).isTrue();
        assertThat(line.isSameName("otherName")).isFalse();
    }

    @Test
    @DisplayName("isSameName 메서드는 null이 들어올 경우 예외를 발생시킨다")
    void isSameName_nullException() {
        Line line = new Line("test", "BLUE");

        assertThatThrownBy(() -> line.isSameName(null))
                .isInstanceOf(NullPointerException.class);
    }
}
