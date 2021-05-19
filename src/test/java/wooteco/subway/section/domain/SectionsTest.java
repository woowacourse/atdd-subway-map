package wooteco.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Sections sections;
    private final Station firstStation = new Station(1L, "해운대역");
    private final Station secondStation = new Station(2L, "몽촌토성역");
    private final Station thirdStation = new Station(3L, "광안역");
    private final Station fourthStation = new Station(4L, "잠실역");
    private final Station fifthStation = new Station(5L, "잠실나루역");

    @BeforeEach
    void setUp() {
        // 1 - 3 - 2 - 5 - 4
        sections = new Sections(
                Arrays.asList(
                        new Section(1L, 1L, firstStation, thirdStation, 10),
                        new Section(2L, 1L, thirdStation, secondStation, 5),
                        new Section(3L, 1L, secondStation, fifthStation, 20),
                        new Section(4L, 1L, fifthStation, fourthStation, 15)
                )
        );
    }

    @DisplayName("sections에 있는 상행선 하행선들을 상행 종점부터 순서대로 나열한 stations를 반환한다")
    @Test
    void getOrderedStations() {
        List<Station> orderedStations = sections.getOrderedStations();
        List<Station> expectedOrderedStations = Arrays.asList(firstStation, thirdStation, secondStation, fifthStation, fourthStation);
        assertThat(orderedStations).isEqualTo(expectedOrderedStations);
    }

    @DisplayName("sections가 비었으면 true, 아니면 false를 반환한다")
    @Test
    void isEmpty() {
        assertThat(new Sections().isEmpty()).isTrue();
        assertThat(sections.isEmpty()).isFalse();
    }

    @DisplayName("section의 상행선 하행선 둘다 전체 구간 내에 존재하면 true, 아니면 false를 반환한다")
    @Test
    void bothStationsExist() {
        Section newSection = new Section(1L, fifthStation, firstStation, 12);
        assertThat(sections.bothStationsExist(newSection)).isTrue();

        newSection = new Section(1L, firstStation, new Station(6L, "강남역"), 10);
        assertThat(sections.bothStationsExist(newSection)).isFalse();

        newSection = new Section(1L, new Station(6L, "강남역"), new Station(7L, "부산역"), 10);
        assertThat(sections.bothStationsExist(newSection)).isFalse();
    }

    @DisplayName("section의 상행선 하행선 둘다 전체 구간 내에 존재하지 않으면 true, 존재하면 false를 반환한다")
    @Test
    void bothStationsDoNotExist() {
        Section newSection = new Section(1L, fifthStation, firstStation, 12);
        assertThat(sections.bothStationsDoNotExist(newSection)).isFalse();

        newSection = new Section(1L, firstStation, new Station(6L, "강남역"), 10);
        assertThat(sections.bothStationsDoNotExist(newSection)).isFalse();

        newSection = new Section(1L, new Station(6L, "강남역"), new Station(7L, "부산역"), 10);
        assertThat(sections.bothStationsDoNotExist(newSection)).isTrue();
    }

    @DisplayName("주어진 구간의 상행선 하행선 중, 전체 구간 내 존재하는 역을 베이스로 하는 기존 구간을 반환")
    @Test
    void findOriginalSection() {
        Section section = new Section(1L, secondStation, new Station(5L, "강남역"), 4);
        Section originalSection = sections.findOriginalSection(section);
        assertThat(originalSection.getId()).isEqualTo(3L);
    }

    @DisplayName("종점이 바뀌는 구간 추가이면 true, 아니면 false")
    @Test
    void isNotEndStationSave() {
        Section section = new Section(1L, secondStation, new Station(5L, "강남역"), 4);
        assertThat(sections.isNotEndStationSave(section)).isTrue();

        section = new Section(1L, new Station(5L, "강남역"), fourthStation, 4);
        assertThat(sections.isNotEndStationSave(section)).isTrue();

        section = new Section(1L, fourthStation, new Station(5L, "강남역"), 4);
        assertThat(sections.isNotEndStationSave(section)).isFalse();
    }

    @DisplayName("역이 종점이면 true, 아니면 false")
    @Test
    void isEndStation() {
        assertThat(sections.isEndStation(fourthStation)).isTrue();
        assertThat(sections.isEndStation(firstStation)).isTrue();
        assertThat(sections.isEndStation(secondStation)).isFalse();
    }

    @DisplayName("역이 전체구간 내에 존재하면 true, 아니면 false")
    @Test
    void doesStationExist() {
        assertThat(sections.doesStationExist(fourthStation)).isTrue();
        assertThat(sections.doesStationExist(firstStation)).isTrue();
        assertThat(sections.doesStationExist(new Station(6L, "홍대역"))).isFalse();
    }

    @DisplayName("구간이 한개이하로 존재하면 true, 아니면 false")
    @Test
    void isUnableToDelete() {
        assertThat(sections.isUnableToDelete()).isFalse();

        Sections sectionsWithOnlyOneSection = new Sections(
                Collections.singletonList(
                        new Section(1L, 1L, firstStation, thirdStation, 10)
                )
        );
        assertThat(sectionsWithOnlyOneSection.isUnableToDelete()).isTrue();
    }

    // 1 - 3 - 2 - 5 - 4
    @DisplayName("종점이 아닌 역이 삭제될 때, 삭제될 역이 빠지면서 새로운 구간을 생성한다")
    @Test
    void createNewSection() {
        Section newSection = sections.createNewSection(1L, secondStation);
        Section expectedNewSection = new Section(1L, new Station(thirdStation.getId()), new Station(fifthStation.getId()), 25);

        assertThat(newSection).usingRecursiveComparison().isEqualTo(expectedNewSection);
    }
}