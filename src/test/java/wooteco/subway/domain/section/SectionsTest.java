package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import wooteco.subway.domain.station.Station;

@DisplayName("지하철노선 집합")
class SectionsTest {

    private static final int STANDARD_DISTANCE = 5;
    private static final int SMALLER_DISTANCE = 1;
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 선릉역 = new Station(3L, "선릉역");
    private static final Station 삼성역 = new Station(4L, "삼성역");
    private static final List<Section> SECTIONS = List.of(
            new Section(1L, 강남역, 역삼역, STANDARD_DISTANCE),
            new Section(2L, 역삼역, 선릉역, STANDARD_DISTANCE));

    private Sections sections;

    @BeforeEach
    void setUp() {
        this.sections = Sections.sort(SECTIONS);
    }

    @DisplayName("지하철구간은 하나 이상이어야 한다.")
    @Test
    void validateSectionsNotEmpty() {
        assertThatThrownBy(() -> Sections.sort(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철구간은 하나 이상이어야 합니다.");
    }

    @DisplayName("구간을 정렬한다.")
    @Test
    void sortSections() {
        Sections sections = Sections.sort(List.of(
                new Section(1L, 역삼역, 선릉역, STANDARD_DISTANCE),
                new Section(2L, 강남역, 역삼역, STANDARD_DISTANCE)));

        List<Station> expected = List.of(강남역, 역삼역, 선릉역);
        assertThat(sections.getStations()).isEqualTo(expected);
    }

    @DisplayName("상행 종점을 추가한다.")
    @Test
    void appendUpStation() {
        sections.append(new Section(3L, 삼성역, 강남역, SMALLER_DISTANCE));

        List<Station> expected = List.of(삼성역, 강남역, 역삼역, 선릉역);
        assertThat(sections.getStations()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("하행 종점을 추가한다.")
    @Test
    void appendDownStation() {
        sections.append(new Section(3L, 선릉역, 삼성역, SMALLER_DISTANCE));

        List<Station> expected = List.of(강남역, 역삼역, 선릉역, 삼성역);
        assertThat(sections.getStations()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("상행역이 동일한 구간 사이에 구간을 추가한다.")
    @Test
    void appendBetweenSectionsWithSameUpStation() {
        sections.append(new Section(3L, 강남역, 삼성역, SMALLER_DISTANCE));

        List<Station> expected = List.of(강남역, 삼성역, 역삼역, 선릉역);
        assertThat(sections.getStations()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("하행역이 동일한 구간 사이에 구간을 추가한다.")
    @Test
    void appendBetweenSectionsWithSameDownStation() {
        sections.append(new Section(3L, 삼성역, 역삼역, SMALLER_DISTANCE));

        List<Station> expected = List.of(강남역, 삼성역, 역삼역, 선릉역);
        assertThat(sections.getStations()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("상행역과 하행역 모두 포함하고 있으면 추가할 수 없다.")
    @ParameterizedTest
    @MethodSource("providerForAppendUpStationAndDownStationBothExist")
    void appendUpStationAndDownStationBothExist(Section section) {
        assertThatThrownBy(() -> sections.append(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 구간의 상행역과 하행역이 이미 노선에 존재합니다.");
    }

    private static Stream<Arguments> providerForAppendUpStationAndDownStationBothExist() {
        return Stream.of(
                Arguments.of(new Section(3L, 강남역, 역삼역, SMALLER_DISTANCE)),
                Arguments.of(new Section(3L, 역삼역, 강남역, SMALLER_DISTANCE))
        );
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    void appendUpStationAndDownStationNeitherExist() {
        Section section = new Section(3L, new Station(10L, "광교역"),
                new Station(11L, "광교중앙역"), SMALLER_DISTANCE);

        assertThatThrownBy(() -> sections.append(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 구간의 상행역과 하행역이 노선에 존재하지 않습니다.");
    }

    @DisplayName("상행종점역을 제거한다.")
    @Test
    void removeUpStation() {
        sections.remove(강남역);
        assertThat(sections.getStations()).doesNotContain(강남역);
    }

    @DisplayName("하행종점역을 제거한다.")
    @Test
    void removeDownStation() {
        sections.remove(선릉역);
        assertThat(sections.getStations()).doesNotContain(선릉역);
    }

    @DisplayName("중간역을 제거한다.")
    @Test
    void removeMiddleStation() {
        sections.remove(역삼역);
        assertThat(sections.getStations()).doesNotContain(역삼역);
    }

    @DisplayName("노선에 존재하지 않는 역을 제거한다.")
    @Test
    void removeStationNotBelongToLine() {
        Station station = new Station(5L, "제주역");
        assertThatThrownBy(() -> sections.remove(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("노선에 포함되어 있는 역이 아닙니다.");
    }

    @DisplayName("구간이 하나뿐인 노선의 역을 제거한다.")
    @Test
    void removeStationFromLineWithOnlyOneSection() {
        Sections sections = Sections.sort(List.of(new Section(강남역, 역삼역, SMALLER_DISTANCE)));
        assertThatThrownBy(() -> sections.remove(강남역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("노선의 구간이 하나이므로 구간을 삭제할 수 없습니다.");
    }

    @DisplayName("노선 목록을 반환한다.")
    @Test
    void getSections() {
        List<Section> actual = sections.getSections();
        assertThat(actual).isEqualTo(SECTIONS);
    }

    @DisplayName("역 목록을 반환한다.")
    @Test
    void getStations() {
        List<Station> actual = sections.getStations();
        assertThat(actual).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("노선 식별자 목록을 반환한다.")
    @Test
    void getSectionIds() {
        List<Long> expected = SECTIONS.stream()
                .map(Section::getId)
                .collect(Collectors.toUnmodifiableList());

        List<Long> actual = sections.getSectionIds();
        assertThat(actual).isEqualTo(expected);
    }
}
