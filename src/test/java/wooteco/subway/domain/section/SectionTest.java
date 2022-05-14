package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import wooteco.subway.domain.station.Station;

@DisplayName("지하철구간")
class SectionTest {

    private static final Long SECTION_ID = 1L;
    private static final Station UP_STATION = new Station(1L, "강남역");
    private static final Station DOWN_STATION = new Station(2L, "역삼역");
    private static final int DEFAULT_DISTANCE = 10;

    private Section section;

    @BeforeEach
    void setUp() {
        this.section = new Section(SECTION_ID, UP_STATION, DOWN_STATION, DEFAULT_DISTANCE);
    }

    @DisplayName("상행역과 하행역은 동일할 수 없다.")
    @Test
    void validateStationsNotSame() {
        assertThatThrownBy(() -> new Section(UP_STATION, UP_STATION, DEFAULT_DISTANCE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 동일할 수 없습니다.");
    }

    @DisplayName("구간에 포함된 역인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForContainsStation")
    void containsStation(Station station, boolean expected) {
        boolean actual = section.containsStation(station);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForContainsStation() {
        return Stream.of(
                Arguments.of(UP_STATION, true),
                Arguments.of(DOWN_STATION, true),
                Arguments.of(new Station("선릉역"), false));
    }

    @DisplayName("구간의 상행역인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEqualsUpStation")
    void equalsUpStation(Station upStation, boolean expected) {
        boolean actual = section.equalsUpStation(upStation);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("구간의 상행역인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEqualsUpStation")
    void equalsUpStationById(Station upStation, boolean expected) {
        boolean actual = section.equalsUpStation(upStation.getId());
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForEqualsUpStation() {
        return Stream.of(
                Arguments.of(UP_STATION, true),
                Arguments.of(DOWN_STATION, false),
                Arguments.of(new Station("선릉역"), false));
    }

    @DisplayName("구간의 하행역인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEqualsDownStation")
    void equalsDownStation(Station downStation, boolean expected) {
        boolean actual = section.equalsDownStation(downStation);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForEqualsDownStation() {
        return Stream.of(
                Arguments.of(UP_STATION, false),
                Arguments.of(DOWN_STATION, true),
                Arguments.of(new Station("선릉역"), false));
    }

    @DisplayName("거리를 비교한다.")
    @ParameterizedTest
    @MethodSource("provideForIsShorterThan")
    void isShorterThan(int distance, boolean expected) {
        boolean actual = section.isLongerThan(new Section(2L, UP_STATION, DOWN_STATION, distance));
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsShorterThan() {
        return Stream.of(
                Arguments.of(DEFAULT_DISTANCE - 1, true),
                Arguments.of(DEFAULT_DISTANCE, false),
                Arguments.of(DEFAULT_DISTANCE + 1, false));
    }

    @DisplayName("거리의 차를 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForCalculateDifferenceBetween")
    void calculateDifferenceOfDistance(Section other, int expected) {
        int actual = section.calculateDifferenceOfDistance(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForCalculateDifferenceBetween() {
        return Stream.of(
                Arguments.of(new Section(2L, UP_STATION, DOWN_STATION, DEFAULT_DISTANCE - 5), 5),
                Arguments.of(new Section(2L, UP_STATION, DOWN_STATION, DEFAULT_DISTANCE + 6), 6));
    }


    @DisplayName("거리의 합을 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForCalculateSumOfDistance")
    void calculateSumOfDistance(Section other, int expected) {
        int actual = section.calculateSumOfDistance(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForCalculateSumOfDistance() {
        return Stream.of(
                Arguments.of(new Section(2L, UP_STATION, DOWN_STATION, 5), 5 + DEFAULT_DISTANCE),
                Arguments.of(new Section(2L, UP_STATION, DOWN_STATION, 6), 6 + DEFAULT_DISTANCE));
    }

    @DisplayName("식별자를 반환한다.")
    @Test
    void getId() {
        Long actual = section.getId();
        assertThat(actual).isEqualTo(SECTION_ID);
    }

    @DisplayName("상행역을 반환한다.")
    @Test
    void getUpStation() {
        Station actual = section.getUpStation();
        assertThat(actual).isEqualTo(UP_STATION);
    }

    @DisplayName("하행역을 반환한다.")
    @Test
    void getDownStation() {
        Station actual = section.getDownStation();
        assertThat(actual).isEqualTo(DOWN_STATION);
    }

    @DisplayName("거리를 반환한다.")
    @Test
    void getDistance() {
        int actual = section.getDistance();
        assertThat(actual).isEqualTo(DEFAULT_DISTANCE);
    }
}