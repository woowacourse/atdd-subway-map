package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Station 테스트")
class StationTest {
    @DisplayName("역 이름이 20자가 넘어가면 예외")
    @Test
    public void whenNameOverTwentyLetters() {
        //given
        String soLongName = "나는20글자가넘는이름이다엄청나게긴이름이다대박사건";

        //when & then
        assertThatThrownBy(() -> new Station(soLongName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("역 이름은 20자를 초과할 수 없습니다.");
    }

    @DisplayName("역명에 앞뒤 공백을 제거하고 띄어쓰기가 2개 이상이 연속됐을 경우 1개로 줄여준다.")
    @ParameterizedTest
    @MethodSource
    public void trimAndRemoveDuplicatedBlankTest(String rawName, String expectedName) {
        //given & when
        Station station = new Station(rawName);

        //then
        assertThat(station.getName()).isEqualTo(expectedName);
    }

    private static Stream<Arguments> trimAndRemoveDuplicatedBlankTest() {
        return Stream.of(
                Arguments.of(" sadang   station    ", "sadang station"),
                Arguments.of("    kog    mo  station", "kog mo station"),
                Arguments.of(" bill    gates   station ", "bill gates station")
        );
    }
}