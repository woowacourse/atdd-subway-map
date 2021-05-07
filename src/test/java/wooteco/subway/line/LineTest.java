package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.LineSuffixException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @DisplayName("Line 객체를 생성한다.")
    @Test
    void createStation() {
        Line line = new Line("2호선", "초록색");
        assertThat(line).isInstanceOf(Line.class);
    }

    @DisplayName("Line 객체를 생성할 때 접미사가 -선으로 끝나지 않는 경우 예외가 발생한다.")
    @Test
    void createStationException() {
        assertThatThrownBy(() -> {
            new Line("2", "초록색");
        }).isInstanceOf(LineSuffixException.class)
                .hasMessage("-선으로 끝나는 이름을 입력해주세요.");
    }
}