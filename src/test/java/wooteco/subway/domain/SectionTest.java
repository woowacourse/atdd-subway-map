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
}
