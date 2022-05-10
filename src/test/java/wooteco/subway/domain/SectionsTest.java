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
                .hasMessage("기존 노선에 등록할 수 없는 구간입니다.");
    }

    @Test
    @DisplayName("노선의 중간구간에 상행역을 등록할 수 있다.")
    void validAddMiddleSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 10));
        sectionList.add(new Section(1L, 1L, 2L, 3L, 10));
        Sections sections = new Sections(sectionList);

        // when
        sections.add(new Section(1L, 1L, 2L, 4L, 5));

        // then
        assertThat(sections.getSections()).hasSize(3)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(
                        tuple(1L, 2L, 10),
                        tuple(2L, 4L, 5),
                        tuple(4L, 3L, 5)
                );
    }

    @Test
    @DisplayName("노선 아이디와 역 아이디를 통해서 중간에 있는 구간을 제거할 수 있다.")
    void deleteMiddleSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when
        sections.remove(1L, 2L);

        // then
        assertThat(sections.getSections()).hasSize(1)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(
                        tuple(1L, 3L, 10));
    }

    @Test
    @DisplayName("노선 아이디와 역 아이디를 통해서 종점 구간을 제거할 수 있다.")
    void deleteFinalSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when
        sections.remove(1L, 3L);

        // then
        assertThat(sections.getSections()).hasSize(1)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(
                        tuple(1L, 2L, 6));
    }

    @Test
    @DisplayName("구간이 1개 이하일 경우 삭제를 하면 예외가 발생한다.")
    void invalidSizeDeleteMiddleSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        Sections sections = new Sections(sectionList);

        // when & then
        assertThatThrownBy(() -> sections.remove(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간은 1개 이상이 있어야 합니다.");
    }

    @Test
    @DisplayName("등록된 구간들 중에 삭제할 역이 존재하지 않는 경우 예외가 발생한다.")
    void invalidOfNoneStationNumber() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 6));
        sectionList.add(new Section(2L, 1L, 2L, 3L, 4));
        Sections sections = new Sections(sectionList);

        // when & then
        assertThatThrownBy(() -> sections.remove(1L, 4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 역과 관련된 구간이 존재하지 않습니다.");
    }
}
