package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("노선의 이름이 공백인지를 검사한다.")
    @Test
    public void blankNameTest() {
        // given & when & then
        Assertions.assertThatThrownBy(() -> new Line("", "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 색이 공백인지를 검사한다.")
    @Test
    public void blankColorTest() {
        // given & when & then
        Assertions.assertThatThrownBy(() -> new Line("신분당선", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 이름과 색이 공백인지를 검사한다.")
    @Test
    public void blankNameAndColorTest() {
        // given & when & then
        Assertions.assertThatThrownBy(() -> new Line("", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}