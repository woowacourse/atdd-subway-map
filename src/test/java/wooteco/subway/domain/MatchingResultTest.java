package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("MatchingResult 는")
class MatchingResultTest {

    @DisplayName("기존 섹션과 다른 섹션과 역을 비교해서 어느 종류의 역이 같은지를 판단해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} upStation={0} downStation={1} expectedMatchingResult={2}")
    @MethodSource("provideStationAndMatchingResult")
    void matching_Station(final Section newSection, final AddMatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from("ab3");
        assertThat(AddMatchingResult.matchMiddleStation(section, newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideStationAndMatchingResult() {
        return Stream.of(
                Arguments.of(SectionFactory.from("ac3"), AddMatchingResult.ADD_TO_RIGHT),
                Arguments.of(SectionFactory.from("cb3"), AddMatchingResult.ADD_TO_LEFT),
                Arguments.of(SectionFactory.from("ab3"), AddMatchingResult.SAME_SECTION),
                Arguments.of(SectionFactory.from("cd3"), AddMatchingResult.NO_MATCHED)
        );
    }

    @DisplayName("기존 섹션의 출발역과 다른 섹션의 도착역이 같은지를 판단해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} newSection={0} expectedMatchingResult={1}")
    @MethodSource("provideMatchingStartStationSource")
    void matching_Start_Station(final Section newSection, final AddMatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from("ab3");
        assertThat(AddMatchingResult.matchStartStation(section, newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideMatchingStartStationSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from("ca3"), AddMatchingResult.ADD_TO_LEFT),
                Arguments.of(SectionFactory.from("cb3"), AddMatchingResult.NO_MATCHED)
        );
    }

    @DisplayName("기존 섹션의 도착역과 다른 섹션의 출발역이 같은지를 판단해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} newSection={0} expectedMatchingResult={1}")
    @MethodSource("provideMatchingEndStationSource")
    void matching_End_Station(final Section newSection, final AddMatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from("ab3");
        assertThat(AddMatchingResult.matchEndStation(section, newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideMatchingEndStationSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from("ba3"), AddMatchingResult.ADD_TO_RIGHT),
                Arguments.of(SectionFactory.from("cb3"), AddMatchingResult.NO_MATCHED)
        );
    }

}
