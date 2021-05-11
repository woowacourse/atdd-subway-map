package wooteco.subway.domain.line.value.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.NegativeIdException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineIdTest {

    @DisplayName("lineId는 음수일 수 없다.")
    @Test
    void lineId() {
        assertThatThrownBy(() -> new LineId(-1L))
                .isInstanceOf(NegativeIdException.class)
                .hasMessage("id값은 음수일 수 없습니다.");

        LineId lineId = new LineId(0L);
        assertThat(lineId.intValue()).isEqualTo(0L);

        lineId = new LineId(1L);
        assertThat(lineId.intValue()).isEqualTo(1L);
    }

    @Test
    void empty() {
        LineId empty = LineId.empty();

        assertThat(empty.intValue()).isEqualTo(-1);
    }

    @Test
    void intValue() {
        LineId empty = new LineId(10L);

        assertThat(empty.intValue()).isEqualTo(10);
    }

    @Test
    void longValue() {
        LineId empty = new LineId(10L);

        assertThat(empty.longValue()).isEqualTo(10L);
    }

    @Test
    void floatValue() {
        LineId empty = new LineId(10L);

        assertThat(empty.floatValue()).isEqualTo(10F);
    }

    @Test
    void doubleValue() {
        LineId empty = new LineId(10L);

        assertThat(empty.doubleValue()).isEqualTo(10D);
    }
}