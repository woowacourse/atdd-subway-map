package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LineTest {

    @ParameterizedTest
    @DisplayName("노선 이름이 공백이면 예외가 발생한다")
    @ValueSource(strings = {"", " ", "    "})
    void newLine_blankName(final String name) {
        assertThatThrownBy(() -> new Line(name, "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선의 이름이 공백이 되어서는 안됩니다.");
    }

    @Test
    @DisplayName("노선 객체 생성에 성공한다.")
    void newLine() {
        // when
        final Line line = new Line("7호선", "bg-red-600");

        // then
        assertThat(line).isNotNull();
    }
}
