package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("상행 종점 구간을 등록할 수 있다.")
    void validAddFinalFirstSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when
        sections.add(new Section(3L, 1L, 4L, 1L, 5));

        // then
        assertThat(sections.getSections()).hasSize(3)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(
                        tuple(4L, 1L, 5),
                        tuple(1L, 2L, 6),
                        tuple(2L, 3L, 4)
                );
    }

    @Test
    @DisplayName("하행 종점 구건을 등록할 수 있다.")
    void validAddFinalListStation() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when
        sections.add(new Section(3L, 1L, 3L, 4L, 5));

        // then
        assertThat(sections.getSections()).hasSize(3)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(
                        tuple(1L, 2L, 6),
                        tuple(2L, 3L, 4),
                        tuple(3L, 4L, 5)
                );
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우 예외가 발생한다.")
    void invalidAddStationOfSameFirstAndEnd() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when & then
        assertThatThrownBy(() -> sections.add(new Section(3L, 1L, 1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 연결되어 있는 구간입니다.");
    }

    @Test
    @DisplayName("현재 구간들에 하나라도 포함이 안되는 구간을 추가할 경우 예외가 발생한다.")
    void invalidAddStationOfNotIncluded() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when & then
        assertThatThrownBy(() -> sections.add(new Section(4L, 1L, 5L, 4L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 등록되지 않은 역입니다.");
    }
}
