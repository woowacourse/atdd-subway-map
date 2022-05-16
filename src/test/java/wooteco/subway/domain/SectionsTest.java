package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        List<Section> _sections = new ArrayList<>();
        _sections.add(new Section(1L, 1L, 1L, 2L, 10));
        _sections.add(new Section(2L, 1L, 2L, 3L, 10));
        _sections.add(new Section(3L, 1L, 3L, 4L, 10));
        sections = new Sections(_sections);
    }

    @Test
    @DisplayName("하나의 Station을 가지는지 확인한다.")
    void hasOneStation() {
        //given
        //when
        //then
        assertAll(
                () -> assertThat(sections.hasOneStation(1L)).isTrue(),
                () -> assertThat(sections.hasOneStation(2L)).isFalse(),
                () -> assertThat(sections.hasOneStation(3L)).isFalse(),
                () -> assertThat(sections.hasOneStation(4L)).isTrue()
        );
    }

    @Test
    @DisplayName("하나의 Section Id를 가져온다.")
    void sectionId() {
        //given
        //when
        //then
        assertAll(
                () -> assertThat(sections.getSectionId(1L)).isEqualTo(1L),
                () -> assertThat(sections.getSectionId(4L)).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("없는 section Id일 경우 에러를 발생한다.")
    void nonSectionId() {
        //given
        //when
        //then
        assertThatThrownBy(() -> sections.getSectionId(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 삭제하려는 구간이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("삭제할 상행 종점을 가져온다.")
    void getUpStationSection() {
        //given
        //when
        Section section = sections.getUpStationSection(2L);
        //then
        assertThat(section).isEqualTo(new Section(1L, 1L, 1L, 2L, 10));
    }

    @Test
    @DisplayName("삭제할 상행 종점이 없는 경우 에러를 발생한다.")
    void getNonUpStationSection() {
        //given
        //when
        //then
        assertThatThrownBy(() -> sections.getUpStationSection(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 삭제할 대상의 상행종점을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("삭제할 하행 종점을 가져온다.")
    void getDownStationSection() {
        //given
        //when
        Section section = sections.getDownStationSection(1L);
        //then
        assertThat(section).isEqualTo(new Section(1L, 1L, 1L, 2L, 10));
    }

    @Test
    @DisplayName("삭제할 하행 종점이 없는 경우 에러를 발생한다.")
    void getNonDownStationSection() {
        //given
        //when
        //then
        assertThatThrownBy(() -> sections.getDownStationSection(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 삭제할 대상의 하행종점을 찾을 수 없습니다.");
    }
}
