package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Station namSung = new Station("남성역");
    private Station isu = new Station("이수역");
    private Station naebang = new Station("내방역");

    private Sections originalSections;

    @BeforeEach
    void setUp() {
        List<Section> original = new ArrayList<>(Arrays.asList(
                new Section(namSung, isu, 3),
                new Section(isu, naebang, 7)));

        originalSections = new Sections(original);
    }

    @Test
    @DisplayName("등록하려는 구간의 상행이 기존 구간의 상행으로 등록되어 있을 때")
    void addUpSectionTest() {
        Sections sections = new Sections(namSung, naebang, 10);
        sections.add(namSung, isu, 3);

        assertThat(sections).isEqualTo(originalSections);
    }

    @Test
    @DisplayName("등록하려는 구간의 하행이 기존 구간의 하행으로 등록되어 있을 때")
    void addDownSectionTest() {
        Sections sections = new Sections(namSung, naebang, 10);
        sections.add(isu, naebang, 7);

        assertThat(sections).isEqualTo(originalSections);
    }

    @Test
    @DisplayName("등록하려는 구간이 전체 구간의 최상단 구간일 때")
    void addLeftSideTest() {
        Sections sections = new Sections(isu, naebang, 7);
        sections.add(namSung, isu, 3);

        assertThat(sections).isEqualTo(originalSections);
    }

    @Test
    @DisplayName("등록하려는 구간이 전체 구간의 최하단 구간일 때")
    void addRightSideTest() {
        Sections sections = new Sections(namSung, isu, 3);
        sections.add(isu, naebang, 7);

        assertThat(sections).isEqualTo(originalSections);
    }

    @Test
    @DisplayName("구간 중간의 역을 삭제하려 할 때")
    void deleteMiddleOfStationTest() {
        Sections expected = new Sections(namSung, naebang, 10);
        originalSections.delete(isu);
        assertThat(originalSections).isEqualTo(expected);
    }

    @Test
    @DisplayName("최상단 역을 삭제하려 할 때")
    void deleteLeftSideStationTest() {
        Sections expected = new Sections(isu, naebang, 7);
        originalSections.delete(namSung);
        assertThat(originalSections).isEqualTo(expected);
    }

    @Test
    @DisplayName("최하단 역을 삭제하려 할 때")
    void deleteRightSideStationTest() {
        Sections expected = new Sections(namSung, isu, 3);
        originalSections.delete(naebang);
        assertThat(originalSections).isEqualTo(expected);
    }
}