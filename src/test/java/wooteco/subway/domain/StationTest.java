package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @DisplayName("Station의 name은 null이면 안된다.")
    @Test
    void validateNameNull() {
        String name = null;
        assertThatThrownBy(() -> new Station(name)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }

    @DisplayName("Station의 name은 없으면 안된다.")
    @Test
    void validateNameBlank() {
        String name = "";
        assertThatThrownBy(() -> new Station(name)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }

    @DisplayName("Station의 name은 크기가 255보다 클 수 없다.")
    @Test
    void validateNameSize() {
        String name = "a";

        for (int i = 0; i < 255; i++) {
            name += "a";
        }

        String finalName = name;
        assertThatThrownBy(() -> new Station(finalName)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재할 수 없는 이름입니다.");
    }
}
