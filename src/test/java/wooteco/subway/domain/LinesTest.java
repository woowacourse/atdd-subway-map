package wooteco.subway.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinesTest {

    @DisplayName("중복된 line 이름이 들어오면 예외가 발생한다.")
    @Test
    void duplicatedName() {
        Line line1 = Line.of("2호선", "초록색");
        Line line2 = Line.of("1호선", "검은색");
        Lines lines = new Lines(List.of(line1, line2));

        Line line = Line.of("2호선", "파란색");
        Assertions.assertThatThrownBy(() -> lines.checkAbleToAdd(line))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중복된 line 색깔이 들어오면 예외가 발생한다.")
    @Test
    void duplicatedColor() {
        Line line1 = Line.of("2호선", "초록색");
        Line line2 = Line.of("1호선", "검은색");
        Lines lines = new Lines(List.of(line1, line2));

        Line line = Line.of("3호선", "검은색");
        Assertions.assertThatThrownBy(() -> lines.checkAbleToAdd(line))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
