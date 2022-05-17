package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
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
    private static final Station TEMP_STATION = new Station(3L, "선릉역");
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

    @DisplayName("해당 구간의 상행역을 포함하고 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForContainsUpStationOf")
    void containsUpStationOf(Section other, boolean expected) {
        boolean actual = section.containsUpStationOf(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForContainsUpStationOf() {
        return Stream.of(
                Arguments.of(new Section(UP_STATION, DOWN_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(DOWN_STATION, UP_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(TEMP_STATION, UP_STATION, DEFAULT_DISTANCE), false),
                Arguments.of(new Section(TEMP_STATION, DOWN_STATION, DEFAULT_DISTANCE), false));
    }

    @DisplayName("해당 구간의 하행역을 포함하고 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForContainsDownStationOf")
    void containsDownStationOf(Section other, boolean expected) {
        boolean actual = section.containsDownStationOf(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForContainsDownStationOf() {
        return Stream.of(
                Arguments.of(new Section(UP_STATION, DOWN_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(DOWN_STATION, UP_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(UP_STATION, TEMP_STATION, DEFAULT_DISTANCE), false),
                Arguments.of(new Section(DOWN_STATION, TEMP_STATION, DEFAULT_DISTANCE), false));
    }

    @DisplayName("이어지는 이전 구간인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForIsPreviousOf")
    void isPreviousOf(Section other, boolean expected) {
        boolean actual = section.isPreviousOf(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsPreviousOf() {
        return Stream.of(
                Arguments.of(new Section(DOWN_STATION, TEMP_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(TEMP_STATION, UP_STATION, DEFAULT_DISTANCE), false));
    }

    @DisplayName("이어지는 다음 구간인지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForIsNextOf")
    void isNextOf(Section other, boolean expected) {
        boolean actual = section.isNextOf(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsNextOf() {
        return Stream.of(
                Arguments.of(new Section(DOWN_STATION, TEMP_STATION, DEFAULT_DISTANCE), false),
                Arguments.of(new Section(TEMP_STATION, UP_STATION, DEFAULT_DISTANCE), true));
    }

    @DisplayName("상행역이 일치하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEqualsUpStation")
    void equalsUpStation(Section other, boolean expected) {
        boolean actual = section.equalsUpStation(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForEqualsUpStation() {
        return Stream.of(
                Arguments.of(new Section(UP_STATION, TEMP_STATION, DEFAULT_DISTANCE), true),
                Arguments.of(new Section(TEMP_STATION, DOWN_STATION, DEFAULT_DISTANCE), false));
    }

    @DisplayName("하행역이 일치하는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEqualsDownStation")
    void equalsDownStation(Section other, boolean expected) {
        boolean actual = section.equalsDownStation(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForEqualsDownStation() {
        return Stream.of(
                Arguments.of(new Section(UP_STATION, TEMP_STATION, DEFAULT_DISTANCE), false),
                Arguments.of(new Section(TEMP_STATION, DOWN_STATION, DEFAULT_DISTANCE), true));
    }

    @DisplayName("해당 역을 상행역으로 지니고 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForContainsAsUpStation")
    void containsAsUpStation(Station station, boolean expected) {
        boolean actual = section.containsAsUpStation(station);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForContainsAsUpStation() {
        return Stream.of(
                Arguments.of(UP_STATION, true),
                Arguments.of(DOWN_STATION, false),
                Arguments.of(TEMP_STATION, false));
    }

    @DisplayName("해당 역을 하행역으로 지니고 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForContainsAsDownStation")
    void containsAsDownStation(Station station, boolean expected) {
        boolean actual = section.containsAsDownStation(station);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForContainsAsDownStation() {
        return Stream.of(
                Arguments.of(UP_STATION, false),
                Arguments.of(DOWN_STATION, true),
                Arguments.of(TEMP_STATION, false));
    }

    @DisplayName("구간을 쪼개다.")
    @ParameterizedTest
    @MethodSource("provideForSplit")
    void split(Section other, List<Section> expected) {
        List<Section> actual = section.split(other);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideForSplit() {
        return Stream.of(
                Arguments.of(
                        Named.of("기존 구간의 상행역과 일치하는 경우",
                                new Section(UP_STATION, TEMP_STATION, DEFAULT_DISTANCE - 5)),
                        List.of(
                                new Section(UP_STATION, TEMP_STATION, DEFAULT_DISTANCE - 5),
                                new Section(TEMP_STATION, DOWN_STATION, 5))),
                Arguments.of(
                        Named.of("기존 구간의 하행역과 일치하는 경우",
                                new Section(TEMP_STATION, DOWN_STATION, DEFAULT_DISTANCE - 5)),
                        List.of(
                                new Section(UP_STATION, TEMP_STATION, 5),
                                new Section(TEMP_STATION, DOWN_STATION, DEFAULT_DISTANCE - 5))));
    }

    @DisplayName("상행역 또는 하행역이 일치하지 않는 구간으로 기존의 구간을 쪼개다.")
    @Test
    void splitWithNonConnectedSection() {
        Section other = new Section(new Station(5L, "제주"), new Station(6L, "서울"), 5);
        assertThatThrownBy(() -> section.split(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 일치하지 않습니다.");
    }

    @DisplayName("거리가 길거나 같은 구간으로 기존의 구간을 쪼개다.")
    @Test
    void splitWithLongestDistance() {
        Section other = new Section(UP_STATION, TEMP_STATION, 999);
        assertThatThrownBy(() -> section.split(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 길거나 같습니다.");
    }

    @DisplayName("구간을 합치다.")
    @Test
    void mergeWithNonConnectedSection() {
        Section other = new Section(new Station(5L, "제주"), new Station(6L, "서울"), 5);
        assertThatThrownBy(() -> section.merge(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 구간은 이어지지 않았습니다.");
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
