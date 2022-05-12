package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LineColorTest {

    @DisplayName("지하철노선 색상은 공백이 될 수 없다.")
    @ParameterizedTest(name = "{index} 입력 : \"{0}\"")
    @ValueSource(strings = {"", " "})
    void createWithBlankName(String name) {
        assertThatThrownBy(() -> new LineColor(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철노선 색상은 공백이 될 수 없습니다.");
    }

    @DisplayName("지하철노선 색상을 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"red,blue,false", "black,black,true"})
    void equals(String color1, String color2, boolean expected) {
        LineColor lineColor1 = new LineColor(color1);
        LineColor lineColor2 = new LineColor(color2);
        assertThat(lineColor1.equals(lineColor2)).isEqualTo(expected);
    }
}