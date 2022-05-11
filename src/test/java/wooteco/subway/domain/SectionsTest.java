package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.fixture.SectionFixture.sectionBC;
import static wooteco.subway.fixture.SectionFixture.sectionCD;
import static wooteco.subway.fixture.StationFixture.stationA;
import static wooteco.subway.fixture.StationFixture.stationB;
import static wooteco.subway.fixture.StationFixture.stationC;
import static wooteco.subway.fixture.StationFixture.stationD;
import static wooteco.subway.fixture.StationFixture.stationE;
import static wooteco.subway.fixture.StationFixture.stationY;
import static wooteco.subway.fixture.StationFixture.stationZ;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.fixture.SectionsFixture;

class SectionsTest {

    @DisplayName("주어진 sections으로 정렬된 지하철역을 가져온다.")
    @Test
    void getOrderedStations() {
        Sections sections = SectionsFixture.createSections();

        List<Station> stations = sections.getOrderedStations();
        List<Station> expectedStations = new LinkedList<>(List.of(stationA, stationB, stationC, stationD, stationE));

        assertThat(stations).isEqualTo(expectedStations);
    }

    @DisplayName("상행 종점에 추가적으로 구간을 추가할 수 있다.")
    @Test
    void findSectionByAddingSectionInLastUpStation() {
        Sections sections = SectionsFixture.createSections();

        assertThat(sections.findSectionByAddingSection(stationZ, stationA, 11)).isEmpty();
    }

    @DisplayName("하행 종점에 추가적으로 구간을 추가할 수 있다.")
    @Test
    void findSectionByAddingSectionInLastDownStation() {
        Sections sections = SectionsFixture.createSections();

        assertThat(sections.findSectionByAddingSection(stationE, stationZ, 11)).isEmpty();
    }

    @DisplayName("등록된 구간 중간에 새로운 구간을 추가할 수 있다.")
    @ParameterizedTest
    @MethodSource("createStations")
    void findSectionByAddingSectionInMiddleStation(Station upStation, Station downStation, Section updateSection) {
        Sections sections = SectionsFixture.createSections();
        Optional<Section> wrappedSection = sections.findSectionByAddingSection(upStation, downStation, 9);
        assert (wrappedSection).isPresent();

        assertThat(wrappedSection.get()).isEqualTo(updateSection);
    }

    static Stream<Arguments> createStations() {
        return Stream.of(
                Arguments.of(stationC, stationZ, sectionCD),
                Arguments.of(stationZ, stationC, sectionBC)
        );
    }


    @DisplayName("지하철역 2개를 이용해서 구간을 만들려고 할 때 두 지하철역 모두 구간에 등록이 안되어 있다면 예외가 발생한다.")
    @Test
    void CheckAddSectionNotExistStation() {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.findSectionByAddingSection(stationY, stationZ, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 상행과 하행 중 노선에 등록된 지하철이 없습니다.");
    }

    @DisplayName("이미 구간으로 등록되어 있는 지하철역 2개를 이용해서 구간을 만들려고 하면 예외가 발생한다.")
    @Test
    void CheckAddSectionAlreadyExistSection() {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.findSectionByAddingSection(stationA, stationC, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 상행 하행 구간이 이미 연결되어 있는 구간입니다.");
    }

    @DisplayName("구간으로 등록할 수 있는 지하철역 2개이지만, 구간 사이의 거리가 기존의 구간과 맞지 않아서 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("createStationsWithWrongDistance")
    void CheckAddSectionToLongDistance(Station upStation, Station downStation, int distance) {
        Sections sections = SectionsFixture.createSections();

        assertThatThrownBy(() -> sections.findSectionByAddingSection(upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간 사이의 거리가 너무 멉니다.");
    }

    static Stream<Arguments> createStationsWithWrongDistance() {
        return Stream.of(
                Arguments.of(stationA, stationZ, 11),
                Arguments.of(stationB, stationZ, 11),
                Arguments.of(stationZ, stationE, 11),
                Arguments.of(stationZ, stationB, 11)
        );
    }

    @DisplayName("현재 있는 구간들을 이용해서 상행, 하행 지하철 2개 중에서 새롭게 추가되는 지하철을 찾는다.")
    @ParameterizedTest
    @MethodSource("createNewStations")
    void findNewStation(Station upStation, Station downStation, Station newStation) {
        Sections sections = SectionsFixture.createSections();

        assertThat(sections.findNewStation(upStation, downStation)).isEqualTo(newStation);
    }

    static Stream<Arguments> createNewStations() {
        return Stream.of(
                Arguments.of(stationZ, stationA, stationZ),
                Arguments.of(stationA, stationZ, stationZ),
                Arguments.of(stationB, stationZ, stationZ),

                Arguments.of(stationZ, stationE, stationZ),
                Arguments.of(stationZ, stationB, stationZ),
                Arguments.of(stationE, stationZ, stationZ)
        );
    }
}
