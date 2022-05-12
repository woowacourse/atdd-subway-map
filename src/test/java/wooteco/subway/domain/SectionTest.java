package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.SectionResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @DisplayName("sections 내부에 section이 동일하게 존재하면 true를 반환한다.")
    @Test
    void isExistedEquallyIn() {
        //given
        Section section = new Section(10, 1L, 1L, 2L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);

        List<Section> sections = List.of(existedSection1, existedSection2);

        //when
        boolean existedIn = section.isExistedIn(sections);

        //then
        assertThat(existedIn).isTrue();

    }

    @DisplayName("sections 내부에 section과 겹치는 구간이 있는 section이 존재하면 true를 반환한다.")
    @Test
    void isExistedLinearlyIn() {
        //given
        Section section = new Section(10, 1L, 1L, 4L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);
        Section existedSection3 = new Section(10, 2L, 3L, 4L);

        List<Section> sections = List.of(existedSection1, existedSection2, existedSection3);

        //when
        boolean existedIn = section.isExistedIn(sections);

        //then
        assertThat(existedIn).isTrue();
    }

    @DisplayName("section의 상행 종점역으로 추가가 가능하면 true 반환")
    @Test
    void isUpperLastStop() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(10, 2L, 4L, 5L);

        boolean isLastStop = newSection.canAddAsLastStation(sections);

        assertThat(isLastStop).isTrue();
    }


    @DisplayName("section의 하행 종점역으로 추가가 가능하면 true 반환")
    @Test
    void isLowerLastStop() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(10, 2L, 6L, 1L);

        boolean isLastStop = newSection.canAddAsLastStation(sections);

        assertThat(isLastStop).isTrue();
    }

    /*
    @DisplayName("[상행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createUpSectionBySections() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 5L, 6L);

        Section generated = Section.createBySections(existed, inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(6L);
            assertThat(generated.getDownStationId()).isEqualTo(4L);
        });
    }*/

    //new
    @DisplayName("[상행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createUpSectionBySections() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 5L, 6L);

        Section generated = existed.createSection(inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(6L);
            assertThat(generated.getDownStationId()).isEqualTo(4L);
        });
    }


    //Deprecated
    @DisplayName("[하행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createDownSectionBySectionsDeprecated() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 6L, 4L);

        Section generated = Section.createBySections(existed, inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(5L);
            assertThat(generated.getDownStationId()).isEqualTo(6L);
        });
    }

    //new
    @DisplayName("[하행]기존에 존재하던 구간과 요청한 구간으로 새 구간을 만든다.")
    @Test
    void createDownSectionBySections() {
        Section existed = new Section(10, 2L, 5L, 4L);
        Section inserted = new Section(4, 2L, 6L, 4L);

        Section generated = existed.createSection(inserted);

        assertAll(() -> {
            assertThat(generated.getDistance()).isEqualTo(6);
            assertThat(generated.getUpStationId()).isEqualTo(5L);
            assertThat(generated.getDownStationId()).isEqualTo(6L);
        });
    }

    @DisplayName("상행 구간 사이 갈림길이 모든 조건이 충족하면 추가할 수 있다.")
    @Test
    void createBetweenUpSection() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(4, 2L, 1L, 5L);

        SectionResult sectionResult = newSection.canAddAsBetweenStation(sections);

        assertThat(sectionResult.canAddAsBetweenStation()).isTrue();
    }

    @DisplayName("상행 구간 사이 갈림길이 길이 조건이 충족하지 않으면 False를 반환한다.")
    @Test
    void createBetweenUpSectionBadDistance() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(10, 2L, 1L, 5L);

        SectionResult sectionResult = newSection.canAddAsBetweenStation(sections);

        assertThat(sectionResult.canAddAsBetweenStation()).isFalse();
    }

    @DisplayName("하행 구간 사이 갈림길이 모든 조건이 충족하면 추가할 수 있다.")
    @Test
    void createBetweenUpSectionSameLine() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(4, 2L, 5L, 3L);

        SectionResult sectionResult = newSection.canAddAsBetweenStation(sections);

        assertThat(sectionResult.canAddAsBetweenStation()).isTrue();
    }

    @DisplayName("하행 구간 사이 갈림길이 길이 조건이 충족하지 않으면 False를 반환한다.")
    @Test
    void createBetweenDownSectionBadDistance() {
        Section section1 = new Section(10, 2L, 1L, 3L);
        Section section2 = new Section(10, 2L, 3L, 2L);
        Section section3 = new Section(10, 2L, 2L, 4L);

        Sections sections = new Sections(List.of(section1, section2, section3));

        Section newSection = new Section(10, 2L, 5L, 3L);

        SectionResult sectionResult = newSection.canAddAsBetweenStation(sections);

        assertThat(sectionResult.canAddAsBetweenStation()).isFalse();
    }
}
