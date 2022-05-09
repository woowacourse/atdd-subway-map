package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Section 은")
public class SectionTest {

    @DisplayName("다른 Section 인스턴스와 역을 비교해서")
    @ParameterizedTest(name = "{index} {displayName} upStation={0} downStation={1} expectedMatchingResult={2}")
    @MethodSource("provideStationAndMatchingResult")
    void matching_Station(final Section newSection, final MatchingResult expectedMatchingResult) {
        final Section section = SectionFactory.from("1a2b");
        assertThat(section.match(newSection)).isEqualTo(expectedMatchingResult);
    }

    private static Stream<Arguments> provideStationAndMatchingResult() {
        return Stream.of(
                Arguments.of(SectionFactory.from("1a3c"), MatchingResult.SAME_UP_STATION),
                Arguments.of(SectionFactory.from("3c2b"), MatchingResult.SAME_DOWN_STATION),
                Arguments.of(SectionFactory.from("1a2b"), MatchingResult.SAME_SECTION),
                Arguments.of(SectionFactory.from("3c4d"), MatchingResult.NO_MATCHED)
        );
    }

}
