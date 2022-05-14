package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.domain.factory.SectionFactory.AB3;
import static wooteco.subway.domain.factory.SectionFactory.AC3;
import static wooteco.subway.domain.factory.SectionFactory.BA3;
import static wooteco.subway.domain.factory.SectionFactory.CA3;
import static wooteco.subway.domain.factory.SectionFactory.CB3;
import static wooteco.subway.domain.factory.SectionFactory.CD3;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.domain.factory.SectionFactory;

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
                Arguments.of(SectionFactory.from(AC3), AddMatchingResult.ADD_TO_RIGHT),
                Arguments.of(SectionFactory.from(CB3), AddMatchingResult.ADD_TO_LEFT),
                Arguments.of(SectionFactory.from(AB3), AddMatchingResult.SAME_SECTION),
                Arguments.of(SectionFactory.from(CD3), AddMatchingResult.NO_MATCHED)
        );
    }

    @DisplayName("기존 섹션의 출발역과 다른 섹션의 도착역이 같은지를 판단해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} newSection={0} expectedMatchingResult={1}")
    @MethodSource("provideMatchingStartStationSource")
    void matching_Start_Station(final Section newSection, final AddMatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from(AB3);
        assertThat(AddMatchingResult.matchStartStation(section, newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideMatchingStartStationSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from(CA3), AddMatchingResult.ADD_TO_LEFT),
                Arguments.of(SectionFactory.from(CB3), AddMatchingResult.NO_MATCHED)
        );
    }

    @DisplayName("기존 섹션의 도착역과 다른 섹션의 출발역이 같은지를 판단해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} newSection={0} expectedMatchingResult={1}")
    @MethodSource("provideMatchingEndStationSource")
    void matching_End_Station(final Section newSection, final AddMatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from(AB3);
        assertThat(AddMatchingResult.matchEndStation(section, newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideMatchingEndStationSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from(BA3), AddMatchingResult.ADD_TO_RIGHT),
                Arguments.of(SectionFactory.from(CB3), AddMatchingResult.NO_MATCHED)
        );
    }

}
