package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionAddManagerTest {

    @DisplayName("객체가 생성될 때 section이 정렬된다.")
    @Test
    void initTest() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 6L, 7L, 5);
        Section section3 = Section.of(1L, 4L, 5L, 5);
        Section section4 = Section.of(1L, 1L, 2L, 5);
        Section section5 = Section.of(1L, 3L, 4L, 5);
        Section section6 = Section.of(1L, 5L, 6L, 5);

        List<Section> sections = List.of(section1, section2, section3, section4, section5, section6);
        SectionAddManager sectionAddManager = SectionAddManager.of(sections);
        List<Section> ordered = sectionAddManager.getOrderedSections();
        assertThat(ordered.get(5).getUpStationId()).isEqualTo(1L);
        assertThat(ordered.get(4).getUpStationId()).isEqualTo(2L);
        assertThat(ordered.get(3).getUpStationId()).isEqualTo(3L);
        assertThat(ordered.get(2).getUpStationId()).isEqualTo(4L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(5L);
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(6L);
    }

    @DisplayName("upStation, downStation 모두 존재하는 section에 포함돼 있으면 예외가 발생한다.")
    @Test
    void addFail_alreadyContainedStations() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 6L, 7L, 5);
        Section section3 = Section.of(1L, 4L, 5L, 5);
        Section section4 = Section.of(1L, 1L, 2L, 5);
        Section section5 = Section.of(1L, 3L, 4L, 5);
        Section section6 = Section.of(1L, 5L, 6L, 5);

        List<Section> sections = List.of(section1, section2, section3, section4, section5, section6);
        SectionAddManager sectionAddManager = SectionAddManager.of(sections);

        Section section = Section.of(1L, 2L, 5L, 3);
        assertThatThrownBy(() -> sectionAddManager.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("upStation, downStation 모두 존재하는 section에 없으면 예왜가 발생한다..")
    @Test
    void addFail_notContainedStations() {
        Section section1 = Section.of(1L, 2L, 3L, 5);
        Section section2 = Section.of(1L, 6L, 7L, 5);
        Section section3 = Section.of(1L, 4L, 5L, 5);
        Section section4 = Section.of(1L, 1L, 2L, 5);
        Section section5 = Section.of(1L, 3L, 4L, 5);
        Section section6 = Section.of(1L, 5L, 6L, 5);

        List<Section> sections = List.of(section1, section2, section3, section4, section5, section6);
        SectionAddManager sectionAddManager = SectionAddManager.of(sections);

        Section section = Section.of(1L, 8L, 12L, 3);
        assertThatThrownBy(() -> sectionAddManager.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_upstream")
    @Test
    void addFail_largeDistance() {
        Section section1 = Section.of(1L, 1L, 2L, 5);
        Section section2 = Section.of(1L, 2L, 3L, 5);

        List<Section> sections = List.of(section1, section2);
        SectionAddManager sectionAddManager = SectionAddManager.of(sections);

        Section section = Section.of(1L, 1L, 5L, 7);
        assertThatThrownBy(() -> sectionAddManager.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_downstream")
    @Test
    void addFail_largeDistance2() {
        Section section1 = Section.of(1L, 1L, 2L, 5);
        Section section2 = Section.of(1L, 2L, 3L, 5);

        List<Section> sections = List.of(section1, section2);
        SectionAddManager sectionAddManager = SectionAddManager.of(sections);

        Section section = Section.of(1L, 0L, 2L, 7);

        assertThatThrownBy(() -> sectionAddManager.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다")
    @Test
    void addFail_largeDistance3() {
        Section section1 = Section.of(1L, 1L, 7L, 10);
        SectionAddManager sectionAddManager = SectionAddManager.of(List.of(section1));
        Section section2 = Section.of(1L, 1L, 2L, 5);
        sectionAddManager.add(section2);

        Section section = Section.of(1L, 1L, 3L, 7);

        assertThatThrownBy(() -> sectionAddManager.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 종점 연장")
    @Test
    void addUpStation() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        SectionAddManager sectionAddManager = SectionAddManager.of(List.of(section1));

        Section section = Section.of(1L, 0L, 1L, 5);
        sectionAddManager.add(section);

        List<Section> ordered = sectionAddManager.getOrderedSections();
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(1L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(0L);
    }

    @DisplayName("하행 종점 연장")
    @Test
    void addDownStation() {
        Section section1 = Section.of(1L, 1L, 2L, 10);
        SectionAddManager sectionAddManager = SectionAddManager.of(List.of(section1));

        Section section = Section.of(1L, 2L, 3L, 5);
        sectionAddManager.add(section);

        List<Section> ordered = sectionAddManager.getOrderedSections();
        assertThat(ordered.get(0).getUpStationId()).isEqualTo(2L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(1L);
    }

    @DisplayName("여러 section을 추가하고 station이 잘 들어갔는지 확인")
    @Test
    void addMany() {
        Section section1 = Section.of(1L, 1L, 8L, 10);
        Section section2 = Section.of(1L, 1L, 2L, 5);
        Section section3 = Section.of(1L, 1L, 3L, 3);
        Section section4 = Section.of(1L, 7L, 8L, 3);
        Section section5 = Section.of(1L, 0L, 1L, 20);

        SectionAddManager sectionAddManager = SectionAddManager.of(List.of(section1));
        sectionAddManager.add(section2);
        sectionAddManager.add(section3);
        sectionAddManager.add(section4);
        sectionAddManager.add(section5);

        List<Section> ordered = sectionAddManager.getOrderedSections();

        assertThat(ordered.get(0).getUpStationId()).isEqualTo(7L);
        assertThat(ordered.get(1).getUpStationId()).isEqualTo(2L);
        assertThat(ordered.get(2).getUpStationId()).isEqualTo(3L);
        assertThat(ordered.get(3).getUpStationId()).isEqualTo(1L);
        assertThat(ordered.get(4).getUpStationId()).isEqualTo(0L);
    }

}
