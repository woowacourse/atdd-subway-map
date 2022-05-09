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
}
