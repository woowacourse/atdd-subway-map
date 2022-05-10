package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        boolean isLastStop = newSection.canAddAsLastStop(sections);

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

        boolean isLastStop = newSection.canAddAsLastStop(sections);

        assertThat(isLastStop).isTrue();
    }
}
