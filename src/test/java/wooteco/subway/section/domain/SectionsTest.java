package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionUpdateException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @Test
    void getSections() {
    }

    @Test
    @DisplayName("구간 정보가 정렬되는지 테스트")
    void sortSections() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 3L, 4L, 5));
        sectionList.add(new Section(2L, 1L, 1L, 2L, 5));
        sectionList.add(new Section(3L, 1L, 5L, 6L, 5));
        sectionList.add(new Section(4L, 1L, 2L, 3L, 5));
        sectionList.add(new Section(4L, 1L, 4L, 5L, 5));

        Sections sections = new Sections(sectionList);
        System.out.println(sections.getSections().get(0).getUpStationId());
        System.out.println(sections.getSections().get(0).getDownStationId());
        System.out.println(sections.getSections().get(4).getUpStationId());
        System.out.println(sections.getSections().get(4).getDownStationId());
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되있을 경우 테스트")
    void addSectionDuplicatedSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 1L, 2L, 5));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 5));

        Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 1L, 2L, 1)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 2L, 1L, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않을 경우 테스트")
    void addSectionNotContainStation() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 1L, 2L, 5));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 5));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 4L, 5L, 1)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 클 경우 테스트")
    void addSectionLongerThanExistSection() {
        List<Section> sectionList = new ArrayList<>();

        sectionList.add(new Section(1L, 1L, 1L, 2L, 1));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 1));

        Sections sections = new Sections(sectionList);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 1L, 4L, 2)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 1L, 4L, 1)))
                .isInstanceOf(SectionUpdateException.class);
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 1L, 5L, 3L, 2)))
                .isInstanceOf(SectionUpdateException.class);
    }

    @Test
    void getStationsId() {
    }
}