package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {

    @Test
    @DisplayName("객체를 생성한다.")
    void create() {
        final String name = "신분당선";
        final String color = "bg-red-600";

        final Line line = new Line(name, color);

        assertAll(() -> {
            assertThat(line.getName()).isEqualTo(name);
            assertThat(line.getColor()).isEqualTo(color);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("노선 이름이 공백인 경우, 예외를 발생한다.")
    void createEmptyName(final String name) {
        assertThatThrownBy(() -> new Line(name, "bg-red-600"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선 이름은 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("색상이 공백인 경우, 예외를 발생한다.")
    void createEmptyColor(final String color) {
        assertThatThrownBy(() -> new Line("신분당선", color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("색상이 공백일 수 없습니다.");
    }
}
