package wooteco.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionDeleteException;
import wooteco.subway.exception.SectionUpdateException;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
        station5 = new Station(5L, "station5");
    }

    @Test
    @DisplayName("구간 정보가 정렬되는지 테스트")
    void sortSections() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(2L, 1L, station1, station2, 5);
        Section section2 = new Section(3L, 1L, station2, station3, 5);
        Section section3 = new Section(1L, 1L, station3, station4, 5);
        sectionList.add(section3);
        sectionList.add(section1);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);

        assertThat(sections.getSections()).containsExactly(section1, section2, section3);
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되있을 경우 테스트")
    void addSectionDuplicatedSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, station1, station2, 5));
        sectionList.add(new Section(2L, 1L, station2, station3, 5));

        Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station1, station2, 1)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station2, station1, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않을 경우 테스트")
    void addSectionNotContainStation() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, station1, station2, 5));
        sectionList.add(new Section(2L, 1L, station2, station3, 5));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station4, station5, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 클 경우 테스트")
    void addSectionLongerThanExistSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, station1, station2, 1));
        sectionList.add(new Section(2L, 1L, station2, station3, 1));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station1, station4, 2)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station1, station4, 1)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, station5, station3, 2)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void deleteSection() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(1L, 1L, station1, station2, 5);
        Section section2 = new Section(2L, 1L, station2, station3, 5);
        Section section3 = new Section(3L, 1L, station3, station4, 5);
        sectionList.add(section1);
        sectionList.add(section2);
        sectionList.add(section3);

        Sections sections = new Sections(sectionList);

        sections.deleteSection(section3.getLineId(), section3.getDownStation());

        assertThat(sections.getSections()).hasSize(2);
        assertThat(sections.getSections()).containsExactly(section1, section2);
    }

    @Test
    @DisplayName("구간이 하나일 경우 삭제 에러")
    void deleteSectionFail() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(1L, 1L, station1, station2, 5);
        sectionList.add(section1);

        Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> sections.deleteSection(section1.getLineId(), station1))
                .isInstanceOf(SectionDeleteException.class);
    }
}