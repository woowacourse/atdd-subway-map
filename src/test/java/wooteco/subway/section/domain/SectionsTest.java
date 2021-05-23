package wooteco.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.sectionsexception.SectionDeleteException;
import wooteco.subway.exception.sectionsexception.SectionUpdateException;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Station 강남역;
    private Station 잠실역;
    private Station 역삼역;
    private Station 교대역;
    private Station 수원역;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        잠실역 = new Station(2L, "잠실역");
        역삼역 = new Station(3L, "역삼역");
        교대역 = new Station(4L, "교대역");
        수원역 = new Station(5L, "수원역");
    }

    @Test
    @DisplayName("구간 정보가 정렬되는지 테스트")
    void sortSections() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(2L, 1L, 강남역, 잠실역, 5);
        Section section2 = new Section(3L, 1L, 잠실역, 역삼역, 5);
        Section section3 = new Section(1L, 1L, 역삼역, 교대역, 5);
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

        sectionList.add(new Section(1L, 1L, 강남역, 잠실역, 5));
        sectionList.add(new Section(2L, 1L, 잠실역, 역삼역, 5));

        Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 강남역, 잠실역, 1)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 잠실역, 강남역, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않을 경우 테스트")
    void addSectionNotContainStation() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 강남역, 잠실역, 5));
        sectionList.add(new Section(2L, 1L, 잠실역, 역삼역, 5));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 교대역, 수원역, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 클 경우")
    void addSectionLongerThanExistSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 강남역, 잠실역, 1));
        sectionList.add(new Section(2L, 1L, 잠실역, 역삼역, 1));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 강남역, 교대역, 2)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 수원역, 역삼역, 2)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이 길이와 같을 경우")
    void addSectionSameDistanceExistSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 강남역, 잠실역, 1));
        sectionList.add(new Section(2L, 1L, 잠실역, 역삼역, 1));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 강남역, 교대역, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void deleteSection() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(1L, 1L, 강남역, 잠실역, 5);
        Section section2 = new Section(2L, 1L, 잠실역, 역삼역, 5);
        Section section3 = new Section(3L, 1L, 역삼역, 교대역, 5);
        sectionList.add(section1);
        sectionList.add(section2);
        sectionList.add(section3);

        Sections sections = new Sections(sectionList);

        sections.findUpdateSectionAfterDelete(section3.getLineId(), section3.getDownStation());

        assertThat(sections.getSections())
                .hasSize(2)
                .containsExactly(section1, section2);
    }

    @Test
    @DisplayName("구간이 하나일 경우 삭제 에러")
    void deleteSectionFail() {
        List<Section> sectionList = new ArrayList<>();

        Section section1 = new Section(1L, 1L, 강남역, 잠실역, 5);
        sectionList.add(section1);

        Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> sections.findUpdateSectionAfterDelete(section1.getLineId(), 강남역))
                .isInstanceOf(SectionDeleteException.class);
    }
}