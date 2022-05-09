package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("section 리스트에서 station id를 추출한다")
    @Test
    void extractStationIds() {
        Section section1 = Section.of(1L, 1L, 2L, 3);
        Section section2 = Section.of(1L, 2L, 3L, 3);
        Section section3 = Section.of(1L, 3L, 4L, 3);

        Sections sections = new Sections(List.of(section1, section2, section3));
        Set<Long> stationIds = sections.extractStationIds();

        assertThat(stationIds.size()).isEqualTo(4);
        assertThat(stationIds.contains(1L)).isTrue();
        assertThat(stationIds.contains(2L)).isTrue();
        assertThat(stationIds.contains(3L)).isTrue();
        assertThat(stationIds.contains(4L)).isTrue();
    }

    @DisplayName("객체가 생성될 때 section이 정렬된다.")
    @Test
    void initTest() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 6L, 7L, 5);
        Section section3 = Section.of(1L, 4L, 5L, 5);
        Section section4 = Section.of(1L, 1L, 2L, 5);
        Section section5 = Section.of(1L, 3L, 4L, 5);
        Section section6 = Section.of(1L, 5L, 6L, 5);

        Sections sections = new Sections(List.of(section1, section2, section3, section4, section5, section6));
        List<Section> ordered = sections.getSections();

        assertThat(ordered.get(5).getUpStationId()).isEqualTo(1L);
        assertThat(ordered.get(4).getUpStationId()).isEqualTo(2L);
        assertThat(ordered.get(3).getUpStationId()).isEqualTo(3L);
        assertThat(ordered.get(2).getUpStationId()).isEqualTo(4L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(5L);
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(6L);
    }

    @DisplayName("upStation, downStation 모두 존재하는 sections에 포함돼 있으면 예외가 발생한다.")
    @Test
    void addFail_alreadyContainedStations() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 1L, 2L, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(1L, 1L, 3L, 3);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
    }

    @DisplayName("upStation, downStation 모두 sections에 없으면 예외가 발생한다..")
    @Test
    void addFail_notContainedStations() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 1L, 2L, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(1L, 4L, 5L, 3);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_upstream")
    @Test
    void addFail_largeDistance() {
        Section section1 = Section.of(1L, 1L, 2L, 5);
        Section section2 = Section.of(1L, 2L, 3L, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(1L, 1L, 5L, 7);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 간 거리는 존재하는 section의 역 간 거리보다 작아야 합니다.");
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_downstream")
    @Test
    void addFail_largeDistance2() {
        Section section1 = Section.of(1L, 1L, 2L, 5);
        Section section2 = Section.of(1L, 2L, 3L, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(1L, 0L, 2L, 7);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다")
    @Test
    void addFail_largeDistance3() {
        Section section1 = Section.of(1L, 1L, 7L, 10);
        Sections sections = new Sections(List.of(section1));
        Section section2 = Section.of(1L, 1L, 2L, 5);
        sections.add(section2);

        Section section = Section.of(1L, 1L, 3L, 7);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 종점 연장")
    @Test
    void addUpStation() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Sections sections = new Sections(List.of(section1));

        Section section = Section.of(1L, 0L, 1L, 5);
        sections.add(section);

        List<Section> ordered = sections.getSections();
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(1L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(0L);
    }

    @DisplayName("하행 종점 연장")
    @Test
    void addDownStation() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Sections sections = new Sections(List.of(section1));

        Section section = Section.of(1L, 2L, 3L, 5);
        sections.add(section);

        List<Section> ordered = sections.getSections();
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(2L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(1L);
    }

    @DisplayName("노선에 section이 1개이면 section삭제 시 예외가 발생한다.")
    @Test
    void deleteFail() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Sections sections = new Sections(List.of(section1));

        assertThatThrownBy(() -> sections.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 역을 삭제할 수 없습니다. 노선에 역은 최소 2개는 존재해야 합니다.");
    }

    @DisplayName("노선에서 양 끝 종점이 아닌 section을 삭제하면 기존 section이 연결된다.")
    @Test
    void deleteSuccess_NoTerminalSection() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Section section2 = Section.of(1L, 2L, 3L, 10);
        Section section3 = Section.of(1L, 3L, 4L, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        sections.delete(2L);

        List<Section> orderedSections = sections.getSections();
        assertThat(orderedSections.size()).isEqualTo(2);
        assertThat(orderedSections.get(0).getUpStationId()).isEqualTo(3L);
        assertThat(orderedSections.get(0).getDownStationId()).isEqualTo(4L);
        assertThat(orderedSections.get(0).getDistance()).isEqualTo(10);
        assertThat(orderedSections.get(1).getUpStationId()).isEqualTo(1L);
        assertThat(orderedSections.get(1).getDownStationId()).isEqualTo(3L);
        assertThat(orderedSections.get(1).getDistance()).isEqualTo(20);
    }

    @DisplayName("노선에서 상행 종점 section이 삭제한다.")
    @Test
    void deleteSuccess_UpTerminalSection() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Section section2 = Section.of(1L, 2L, 3L, 10);
        Section section3 = Section.of(1L, 3L, 4L, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        sections.delete(1L);

        List<Section> orderedSections = sections.getSections();
        assertThat(orderedSections.size()).isEqualTo(2);
        assertThat(orderedSections.get(0).getUpStationId()).isEqualTo(3L);
        assertThat(orderedSections.get(0).getDownStationId()).isEqualTo(4L);
        assertThat(orderedSections.get(0).getDistance()).isEqualTo(10);
        assertThat(orderedSections.get(1).getUpStationId()).isEqualTo(2L);
        assertThat(orderedSections.get(1).getDownStationId()).isEqualTo(3L);
        assertThat(orderedSections.get(1).getDistance()).isEqualTo(10);
    }

    @DisplayName("노선에서 하행 종점 section이 삭제한다.")
    @Test
    void deleteSuccess_DownTerminalSection() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        Section section2 = Section.of(1L, 2L, 3L, 10);
        Section section3 = Section.of(1L, 3L, 4L, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        sections.delete(4L);

        List<Section> orderedSections = sections.getSections();
        assertThat(orderedSections.size()).isEqualTo(2);
        assertThat(orderedSections.get(0).getUpStationId()).isEqualTo(2L);
        assertThat(orderedSections.get(0).getDownStationId()).isEqualTo(3L);
        assertThat(orderedSections.get(0).getDistance()).isEqualTo(10);
        assertThat(orderedSections.get(1).getUpStationId()).isEqualTo(1L);
        assertThat(orderedSections.get(1).getDownStationId()).isEqualTo(2L);
        assertThat(orderedSections.get(1).getDistance()).isEqualTo(10);
    }
}
