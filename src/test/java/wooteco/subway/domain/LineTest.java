package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    @DisplayName("Line의 name은 null이면 안된다.")
    @Test
    void validateNameNull() {
        String name = null;
        String color = "green";
        assertThatThrownBy(() -> new Line(name, color)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }

    @DisplayName("Line의 name은 없으면 안된다.")
    @Test
    void validateNameBlank() {
        String name = "";
        String color = "green";
        assertThatThrownBy(() -> new Line(name, color)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }

    @DisplayName("Line의 name은 크기가 255보다 클 수 없다.")
    @Test
    void validateNameSize() {
        String name = "a";

        for (int i = 0; i < 255; i++) {
            name += "a";
        }

        String color = "green";
        String finalName = name;
        assertThatThrownBy(() -> new Line(finalName, color)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }

    @DisplayName("Line의 color는 null이면 안된다.")
    @Test
    void validateColorNull() {
        String color = null;
        String name = "2호선";
        assertThatThrownBy(() -> new Line(name, color)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 색상입니다.");
    }

    @DisplayName("Line의 color는 없으면 안된다.")
    @Test
    void validateColorBlank() {
        String color = "";
        String name = "2호선";
        assertThatThrownBy(() -> new Line(name, color)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 색상입니다.");
    }

    @DisplayName("Line의 color는 크기가 20보다 클 수 없다.")
    @Test
    void validateColorSize() {
        String color = "a";

        for (int i = 0; i < 20; i++) {
            color += "a";
        }

        String name = "2호선";
        String finalColor = color;
        assertThatThrownBy(() -> new Line(name, finalColor)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 색상입니다.");
    }
}
