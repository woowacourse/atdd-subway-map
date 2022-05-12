package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LineNameTest {

    @DisplayName("지하철노선 이름은 공백이 될 수 없다.")
    @ParameterizedTest(name = "{index} 입력 : \"{0}\"")
    @ValueSource(strings = {"", " "})
    void createWithBlankName(String name) {
        assertThatThrownBy(() -> new LineName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철노선 이름은 공백이 될 수 없습니다.");
    }

    @DisplayName("지하철노선 이름을 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,분당선,false", "분당선,분당선,true"})
    void equals(String name1, String name2, boolean expected) {
        LineName lineName1 = new LineName(name1);
        LineName lineName2 = new LineName(name2);
        assertThat(lineName1.equals(lineName2)).isEqualTo(expected);
    }
}