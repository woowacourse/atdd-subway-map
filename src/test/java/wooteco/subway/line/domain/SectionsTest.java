package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.rule.FindSectionHaveSameDownRule;
import wooteco.subway.line.domain.rule.FindSectionHaveSameUpRule;
import wooteco.subway.line.domain.rule.FindSectionRule;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static wooteco.subway.line.domain.Sections.ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE;

public class SectionsTest {

    @DisplayName("Sections는 정렬된 Section List를 가진다.")
    @Test
    public void testSort() {
        //given
        Section section1 = new Section(1L, 2L, 10);
        Section section2 = new Section(3L, 4L, 10);
        Section section3 = new Section(2L, 3L, 10);

        //when
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        //then
        assertThat(sections.toList())
                .containsExactly(Arrays.asList(section1, section3, section2)
                        .toArray(new Section[0]));
    }

    @DisplayName("상행역과 하행역 둘 중 하나만 노선에 존재해야 합니다.")
    @Test
    public void testValidateEnableAddSection() {
        //given
        Section section1 = new Section(1L, 2L, 10);
        Section section2 = new Section(2L, 3L, 10);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        //when
        Section section3 = new Section(4L, 5L, 10);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            sections.validateEnableAddSection(section3);
        }).withMessageContaining(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE);
    }

    @DisplayName("주어진 Section이 노선의 양끝에 추가 될 수 있는지 판별한다.")
    @Test
    public void testCheckEndPoint() {
        //given
        Section section1 = new Section(1L, 2L, 10);
        Section section2 = new Section(2L, 3L, 10);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        //when
        Section section3 = new Section(4L, 1L, 10);
        Section section4 = new Section(3L, 5L, 10);
        Section section5 = new Section(2L, 6L, 10);

        //then
        assertThat(sections.checkEndPoint(section3)).isTrue();
        assertThat(sections.checkEndPoint(section4)).isTrue();
        assertThat(sections.checkEndPoint(section5)).isFalse();
    }

    @DisplayName("Section이 주어 졌을 때, 제거 되어야 하는 Section을 반환한다.")
    @Test
    public void testFindDeleteByAdding() {
        //given
        Section section1 = new Section(1L, 2L, 10);
        Section section2 = new Section(2L, 3L, 10);
        Sections sections = new Sections(Arrays.asList(section1, section2));

        //when
        Section section3 = new Section(2L, 6L, 10);
        List<FindSectionRule> findSectionRules = Arrays.asList(new FindSectionHaveSameUpRule(),
                new FindSectionHaveSameDownRule());
        Section deletedSection = sections.findDeleteByAdding(section3, findSectionRules);

        //then
        assertThat(deletedSection.getUpStationId()).isEqualTo(2L);
        assertThat(deletedSection.getDownStationId()).isEqualTo(3L);
    }
}
