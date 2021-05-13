package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.exception.InvalidLineNameException;
import wooteco.subway.section.domain.EmptySections;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line 테스트")
class LineTest {
    private static final String TEST_COLOR = "red darken-3";

    @DisplayName("노선 이름이 20자가 넘어가면 예외")
    @Test
    public void whenNameOverTwentyLetters() {
        //given
        String soLongName = "나는20글자가넘는이름이다엄청나게긴이름이다대박사건";

        //when & then
        assertThatThrownBy(() -> new Line(soLongName, TEST_COLOR, new EmptySections()))
                .isInstanceOf(InvalidLineNameException.class)
                .hasMessageContaining("노선 이름은 20자를 초과할 수 없습니다.");
    }

    @DisplayName("노선명에 앞뒤 공백을 제거하고 띄어쓰기가 2개 이상이 연속됐을 경우 1개로 줄여준다.")
    @ParameterizedTest
    @MethodSource
    public void trimAndRemoveDuplicatedBlankTest(String rawName, String expectedName) {
        //given & when
        Line line = new Line(rawName, TEST_COLOR, new EmptySections());

        //then
        assertThat(line.getName().text()).isEqualTo(expectedName);
    }

    private static Stream<Arguments> trimAndRemoveDuplicatedBlankTest() {
        return Stream.of(
                Arguments.of(" sadang  line    ", "sadang line"),
                Arguments.of("    kog mo  line", "kog mo line"),
                Arguments.of(" bill    gates   line ", "bill gates line")
        );
    }

    @DisplayName("노선 이름에 자모음이 아닌 한글, 영어, 숫자, 공백, 괄호, ·가 아닌 문자가 들어가면 예외")
    @ParameterizedTest
    @ValueSource(strings = {"쓰다가 오타가 나버ㄹㄴ노선", "대쉬는-안되는데-노선", "슬래쉬도/안되는데/노선", "꿈은이루어진다★노선"})
    public void invalidNameTest(String invalidName) {
        //given & when & then
        assertThatThrownBy(() -> new Line(invalidName, TEST_COLOR, new EmptySections()))
                .isInstanceOf(InvalidLineNameException.class)
                .hasMessageContaining("노선 이름에 유효하지 않은 문자가 있습니다.");
    }
}