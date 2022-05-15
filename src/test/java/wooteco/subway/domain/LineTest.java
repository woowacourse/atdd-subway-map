package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("노선의 이름이 공백인지를 검사한다.")
    @Test
    public void blankNameTest() {
        assertThatThrownBy(() -> new Line("", "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 색이 공백인지를 검사한다.")
    @Test
    public void blankColorTest() {
        assertThatThrownBy(() -> new Line("신분당선", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 이름과 색이 공백인지를 검사한다.")
    @Test
    public void blankNameAndColorTest() {
        assertThatThrownBy(() -> new Line("", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}