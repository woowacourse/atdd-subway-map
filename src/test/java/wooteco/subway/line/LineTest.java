package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.Distance;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("지하철노선 관련 기능")
public class LineTest {
    private Line line;

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
        Sections sections = new Sections(sectionGroup);
        line = new Line(1L, "A", "Blue", sections);
    }

    @DisplayName("노선의 경로를 조회한다.")
    @Test
    void path() {
        assertThat(line.path()).containsExactly(
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSectionToLine() {
        Station station1 = new Station(1L, "A");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station4, station1, Distance.of(10));
        line.addSection(section);
        assertThat(line.path()).containsExactly(
                new Station(4L, "D"),
                new Station(1L, "A"),
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("노선에 두개의 역이 이미 존재하는 구간을 추가한다.")
    @Test
    void addWrongSectionWithTwoExistingStationsToLine() {
        Station station1 = new Station(1L, "A");
        Station station2 = new Station(2L, "B");
        Section section = new Section(station1, station2, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(section));
    }

    @DisplayName("노선에 두개의 역이 존재하지 않는 구간을 추가한다.")
    @Test
    void addWrongSectionWithNotExistingStationsToLine() {
        Station station4 = new Station(4L, "D");
        Station station5 = new Station(5L, "E");
        Section section = new Section(station4, station5, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(section));
    }

    @DisplayName("노선에 올바르지 않은 길이의 구간을 추가한다.")
    @Test
    void addWrongSectionWithSameDistanceToLine() {
        Station station1 = new Station(1L, "A");
        Station station4 = new Station(4L, "D");
        Section section = new Section(station1, station4, Distance.of(10));
        assertThatIllegalArgumentException().isThrownBy(() -> line.addSection(section));
    }

    @DisplayName("노선에서 구간을 제거한다.")
    @Test
    void deleteSectionFromLine() {
        line.deleteSection(new Station(1L, "A"));
        assertThat(line.path()).containsExactly(
                new Station(2L, "B"),
                new Station(3L, "C")
        );
    }

    @DisplayName("노선에서 존재하지 않는 역의 구간을 제거한다.")
    @Test
    void deleteSectionWithNotExistingStationFromLine() {
        assertThatIllegalArgumentException().isThrownBy(() -> line.deleteSection(new Station(4L, "D")));
    }

    @DisplayName("노선에서 남은 하나의 구간을 제거한다.")
    @Test
    void deleteSectionFromLineWithSingleSection() {
        line.deleteSection(new Station(2L, "B"));
        assertThatIllegalArgumentException().isThrownBy(() -> line.deleteSection(new Station(1L, "A")));
    }
}
