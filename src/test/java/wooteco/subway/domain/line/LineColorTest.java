package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철노선 색상")
class LineColorTest {

    @DisplayName("색상은 공백이 될 수 없다.")
    @ParameterizedTest(name = "{index} 입력 : \"{0}\"")
    @ValueSource(strings = {"", " "})
    void createBlankColor(String color) {
        assertThatThrownBy(() -> new LineColor(color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철노선 색상은 공백이 될 수 없습니다.");
    }

    @DisplayName("색상을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"white"})
    void getColor(String expected) {
        String actual = (new LineColor(expected)).getColor();
        assertThat(actual).isEqualTo(expected);
    }
}
