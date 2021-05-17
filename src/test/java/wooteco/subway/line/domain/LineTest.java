package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.EmptyInputException;
import wooteco.subway.exception.NullInputException;
import wooteco.subway.exception.line.LineSuffixException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @DisplayName("노선 생성 성공")
    @Test
    void line() {
        Line line = new Line("2호선", "초록색");
        assertThat(new Line("2호선", "초록색"))
            .usingRecursiveComparison()
            .isEqualTo(line);
    }

    @DisplayName("null 입력 시 예외 발생")
    @Test
    void nullLine() {
        assertThatThrownBy(() -> new Line(null, null))
            .hasMessage(new NullInputException().getMessage());
    }

    @DisplayName("빈 요소 입력 시 예외 발생")
    @Test
    void emptyNameOrColorLine() {
        assertThatThrownBy(() -> new Line("선", "초록색"))
            .hasMessage(new EmptyInputException().getMessage());

        assertThatThrownBy(() -> new Line("2호선", ""))
            .hasMessage(new EmptyInputException().getMessage());
    }

    @DisplayName("-선 접미사 미입력 시 예외 발생")
    @Test
    void invalidSuffixLine() {
        assertThatThrownBy(() -> new Line("신분당", "초록색"))
            .hasMessage(new LineSuffixException().getMessage());
    }
}
