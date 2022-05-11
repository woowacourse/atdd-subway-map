package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 관련 기능")
public class LineTest {

    @DisplayName("이름이 공백, 빈값이면 예외를 발생시킨다.")
    @Test
    void lineNameException() {
        assertThatThrownBy(() -> new Line("", "red"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선의 이름은 공백, 빈값으로 할 수 없습니다.");
    }

    @DisplayName("색깔이 공백, 빈값이면 예외를 발생시킨다.")
    @Test
    void lineColorException() {
        assertThatThrownBy(() -> new Line("신분당선", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선의 색깔은 공백, 빈값으로 할 수 없습니다.");
    }
}
