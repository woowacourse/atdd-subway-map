package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    void getStationsId() {
    }
}