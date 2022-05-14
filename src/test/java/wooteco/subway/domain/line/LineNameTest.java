package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철노선 이름")
class LineNameTest {

    @DisplayName("이름은 공백이 될 수 없다.")
    @ParameterizedTest(name = "[{index}] 입력 : \"{0}\"")
    @ValueSource(strings = {"", " "})
    void createBlankName(String name) {
        assertThatThrownBy(() -> new LineName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철노선 이름은 공백이 될 수 없습니다.");
    }

    @DisplayName("이름을 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,분당선,false", "분당선,분당선,true"})
    void equals(String name1, String name2, boolean expected) {
        LineName thisName = new LineName(name1);
        LineName otherName = new LineName(name2);

        boolean actual = thisName.equals(otherName);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이름을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"2호선"})
    void getName(String expected) {
        String actual = (new LineName(expected)).getName();
        assertThat(actual).isEqualTo(expected);
    }
}