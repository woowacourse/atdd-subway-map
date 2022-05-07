package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LineTest {

    @Test
    @DisplayName("이름과 색깔로 노선을 생성한다.")
    void create() {
        assertThat(new Line("2호선", "bg-red-600")).isNotNull();
    }

    @ParameterizedTest
    @EmptySource
    @DisplayName("이름과 색깔이 값이 없을 경우 예외를 발생한다.")
    void empty(String value) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Line(value, value))
                .withMessage("이름과 색깔은 공백일 수 없습니다.");
    }

    @ParameterizedTest(name = "이름 : {0}, 메시지 : {1}")
    @CsvSource({"12345678910, 노선 이름은 10글자를 초과할 수 없습니다.", "노선, 노선 이름은 3글자 이상이어야 합니다."})
    @DisplayName("노선 이름이 3글자 미만 10글자 초과일 경우 예외를 발생한다.")
    void invalidName(String value, String message) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Line(value, "blue"))
                .withMessage(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1line", "line", "1호sun"})
    @DisplayName("노선 이름은 한글과 숫자가 아닌 경우 예외를 발생한다.")
    void invalidName(String name) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Line(name, "blue"))
                .withMessage("노선 이름은 한글과 숫자이어야 합니다.");
    }

    @Test
    @DisplayName("id로 노선 정보를 수정한다.")
    void modify() {
        Line line = new Line("2호선", "bg-red-600");
        line.update("3호선", "blue");
        assertThat(line.getName()).isEqualTo("3호선");
    }

    @ParameterizedTest
    @EmptySource
    @DisplayName("수정시 이름과 색깔이 값이 없을 경우 예외를 발생한다.")
    void modifyEmpty(String value) {
        Line line = new Line("2호선", "red");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.update(value, value))
                .withMessage("이름과 색깔은 공백일 수 없습니다.");
    }

    @ParameterizedTest(name = "이름 : {0}, 메시지 : {1}")
    @CsvSource({"12345678910, 노선 이름은 10글자를 초과할 수 없습니다.", "노선, 노선 이름은 3글자 이상이어야 합니다."})
    @DisplayName("노선 이름이 3글자 미만 10글자 초과일 경우 예외를 발생한다.")
    void invalidUpdateName(String value, String message) {
        Line line = new Line("2호선", "blue");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.update(value, "blue"))
                .withMessage(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1line", "line", "1호sun"})
    @DisplayName("노선 이름은 한글과 숫자가 아닌 경우 예외를 발생한다.")
    void invalidUpdateName(String name) {
        Line line = new Line("2호선", "blue");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.update(name, "blue"))
                .withMessage("노선 이름은 한글과 숫자이어야 합니다.");
    }

}
