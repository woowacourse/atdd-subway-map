package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("Sections 관련 기능")
public class SectionsTest {
    private Sections sections;

    @BeforeEach
    void setUp() {
        Station station1 = new Station(1L, "A");
        Station station2 = new Station(2L, "B");
        Station station3 = new Station(3L, "C");

        Set<Section> sectionGroup = new HashSet<>();
        Section section1 = new Section(station1, station2, Distance.of(10));
        sectionGroup.add(section1);
        Section section2 = new Section(station2, station3, Distance.of(10));
        sectionGroup.add(section2);
        sections = new Sections(sectionGroup);
    }

    @DisplayName("Sections의 경로를 조회한다.")
    @Test
    void path() {
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 시작구간 왼쪽으로 구간을 추가한다.")
    @Test
    void addSectionToLeftOfFirstSection() {
        Station station1 = new Station(1L, "A");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station4, station1, Distance.of(10));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(4L, "D"),
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 시작구간 사이에 상행역 기준으로 구간을 추가한다.")
    @Test
    void addSectionBetweenFirstSectionBasedOnUpstation() {
        Station station1 = new Station(1L, "A");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station1, station4, Distance.of(5));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(4L, "D"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 시작구간 사이에 하행역 기준으로 구간을 추가한다.")
    @Test
    void addSectionBetweenFirstSectionBasedOnDownstation() {
        Station station2 = new Station(2L, "B");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station4, station2, Distance.of(5));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(4L, "D"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 마지막구간 사이에 상행역 기준으로 구간을 추가한다.")
    @Test
    void addSectionBetweenLastSectionBasedOnUpstation() {
        Station station2 = new Station(2L, "B");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station2, station4, Distance.of(5));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(4L, "D"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 마지막구간 사이에 하행역 기준으로 구간을 추가한다.")
    @Test
    void addSectionBetweenLastSectionBasedOnDownstation() {
        Station station3 = new Station(3L, "C");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station4, station3, Distance.of(5));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(4L, "D"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections의 마지막구간 오른쪽으로 구간을 추가한다.")
    @Test
    void addSectionToRightOfLastSection() {
        Station station3 = new Station(3L, "C");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station3, station4, Distance.of(10));
        sections.addSection(section);
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(3L, "C"),
                new Station(4L, "D")
        );
    }

    @DisplayName("Sections에 두개의 역이 이미 존재하는 구간을 추가한다.")
    @Test
    void addWrongSectionWithTwoExistingStations() {
        Station station1 = new Station(1L, "A");
        Station station2 = new Station(2L, "B");
        Section section = new Section(station1, station2, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.addSection(section));
    }

    @DisplayName("Sections에 두개의 역이 존재하지 않는 구간을 추가한다.")
    @Test
    void addWrongSectionWithNotExistingStations() {
        Station station4 = new Station(4L, "D");
        Station station5 = new Station(5L, "E");
        Section section = new Section(station4, station5, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.addSection(section));
    }

    @DisplayName("Sections에 올바르지 않은 길이의 구간을 추가한다.")
    @Test
    void addWrongSectionWithSameDistance() {
        Station station1 = new Station(1L, "A");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station1, station4, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.addSection(section));
    }

    @DisplayName("Sections에서 상행 종점역의 구간을 제거한다.")
    @Test
    void deleteFirstStation() {
        sections.deleteStation(new Station(1L, "A"));
        assertThat(sections.path()).containsExactly(
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections에서 중간역의 구간을 제거한다.")
    @Test
    void deleteMiddleStation() {
        sections.deleteStation(new Station(2L, "B"));
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(3L, "C")
        );
    }

    @DisplayName("Sections에서 하행 종점역의 구간을 제거한다.")
    @Test
    void deleteLastStation() {
        sections.deleteStation(new Station(3L, "C"));
        assertThat(sections.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B")
        );
    }

    @DisplayName("Sections에서 존재하지 않는 역의 구간을 제거한다.")
    @Test
    void deleteNotExistingStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(new Station(4L, "D")));
    }

    @DisplayName("Sections에서 남은 하나의 구간을 제거한다.")
    @Test
    void deleteStationFromSingleSection() {
        sections.deleteStation(new Station(2L, "B"));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(new Station(1L, "A")));
    }
}
