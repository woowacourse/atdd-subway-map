package wooteco.subway.domain.line.value.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.NameLengthException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineColorTest {

    @DisplayName("LineColor의 글자 수는 1 이상이어야 한다.")
    @Test
    void lineColor() {
        assertThatThrownBy(() -> new LineColor(""))
                .isInstanceOf(NameLengthException.class)
                .hasMessage("이름은 0 보다 커야 합니다.");
    }

    @DisplayName("Line Color를 스트링으로 반환한다")
    @Test
    void asString() {
        LineColor red = new LineColor("red");

        assertThat(red.asString()).isEqualTo("red");
    }

}