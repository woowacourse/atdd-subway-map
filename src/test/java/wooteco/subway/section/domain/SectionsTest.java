package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.exception.SectionsIllegalArgumentException;
import wooteco.subway.section.exception.SectionsSizeTooSmallException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.section.domain.Fixture.*;

@DisplayName("구간 일급 컬렉션 기능 테스트")
class SectionsTest {
    @DisplayName("노선구간의 생성")
    @Test
    void createSections() {
        //given
        //when
        //then
        assertThat(new Sections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION))
                .isNotNull();
    }

    @DisplayName("사이즈가 2보다 작은 구간 일급 컬렉션을 만들려 하면 예외")
    @Test
    void whenCreateTooSmallSections() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Sections(Collections.emptyList()))
                .isInstanceOf(SectionsSizeTooSmallException.class);
    }

    @DisplayName("노선구간의 종점이 1개가 아니면 예외")
    @Test
    void whenEndStationsSizeNotOne() {
        //given
        //when
        //then
        assertThatThrownBy(() -> new Sections(FIRST_SECTION, DOUBLE_END_UPSTATION_SECTION))
                .isInstanceOf(SectionsIllegalArgumentException.class);
    }

    @DisplayName("노선 구간을 생성시 상행종점역 -> 하행종점역 방면으로 정렬되는지 확인")
    @Test
    void sortSectionsTest() {
        //given
        //when
        Sections sections = new Sections(SECOND_SECTION, FIRST_SECTION, FOURTH_SECTION, THIRD_SECTION);
        //then
        assertThat(sections.getSections()).containsExactly(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION);
    }

    @DisplayName("역정렬 조회 기능 테스트")
    @Test
    void getReverseTest() {
        //given
        //when
        Sections sections = new Sections(SECOND_SECTION, FIRST_SECTION, FOURTH_SECTION, THIRD_SECTION);
        //then
        assertThat(sections.getReverseSections()).containsExactly(FOURTH_SECTION, THIRD_SECTION, SECOND_SECTION, FIRST_SECTION);
    }
}