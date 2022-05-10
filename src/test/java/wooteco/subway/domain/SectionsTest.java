package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionException;

class SectionsTest {

    @DisplayName("새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.")
    @Test
    public void addNewSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, 3L, 1));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Section section = new Section(2L, 1L, 1L, 2L, 1);
        assertDoesNotThrow(() -> sections.add(section));
    }
    
    @DisplayName("하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다.")
    @Test
    public void forkRode() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 3L, 7));
        final Sections sections = new Sections(sectionList);

        // when
        final Section section = new Section(2L, 1L, 1L, 2L, 4);
        sections.add(section);

        // then
        assertThat(sections.getSections().size()).isEqualTo(2);
        assertThat(sections.getSections()).extracting("distance")
                .containsExactlyInAnyOrder(4, 3);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    public void checkDistance() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 3L, 7));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Section section = new Section(2L, 1L, 1L, 2L, 7);
        assertThatThrownBy(() -> sections.add(section))
                        .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    public void sameSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 3L, 7));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Section section = new Section(2L, 1L, 1L, 3L, 7);
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    public void IllegalAddSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, 3L, 4));
        sectionList.add(new Section(1L, 1L, 1L, 2L, 3));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Section section = new Section(2L, 1L, 4L, 5L, 7);
        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalSectionException.class);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("Station을 받아 구간을 제거할 수 있다.")
    @Test
    public void deleteSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 2L, 3));
        sectionList.add(new Section(1L, 1L, 2L, 3L, 4));
        final Sections sections = new Sections(sectionList);

        // when
        final Station station = new Station(2L, "중간역");
        sections.delete(station);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        final Section section = sections.getSections().get(0);
        assertThat(section.getDistance()).isEqualTo(7);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.")
    @Test
    public void IllegalDeleteSection() {
        // given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 1L, 3L, 7));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Station station = new Station(1L, "상행역");
        assertThatThrownBy(() -> sections.delete(station))
                .isInstanceOf(IllegalSectionException.class);
    }
}