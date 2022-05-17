package wooteco.subway.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinesTest {

    @DisplayName("중복된 line 이름이 들어오면 예외가 발생한다.")
    @Test
    void duplicatedName() {
        Line line1 = new Line.Builder("2호선", "초록색")
                .build();
        Line line2 = new Line.Builder("1호선", "검은색")
                .build();
        Lines lines = new Lines(List.of(line1, line2));

        Line line = new Line.Builder("2호선", "파란색")
                .build();
        Assertions.assertThatThrownBy(() -> lines.checkAbleToAdd(line))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중복된 line 색깔이 들어오면 예외가 발생한다.")
    @Test
    void duplicatedColor() {
        Line line1 = new Line.Builder("2호선", "초록색")
                .build();
        Line line2 = new Line.Builder("1호선", "검은색")
                .build();
        Lines lines = new Lines(List.of(line1, line2));

        Line line = new Line.Builder("3호선", "검은색")
                .build();
        Assertions.assertThatThrownBy(() -> lines.checkAbleToAdd(line))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
