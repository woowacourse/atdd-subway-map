package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("1 - 2 - 3의 구간에서 2 - 4의 구간이 중간 구간인지 판별한다.")
    void isMiddleSection() {
        // given
        Sections sections = createSections();
        Section section = new Section(2L, 4L);

        // when
        boolean result = sections.isMiddleSection(section);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void hasStationId() {
        // given
        Sections sections = createSections();

        // when
        boolean result = sections.hasStationId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void findSectionByUpStationId() {
        // given
        Sections sections = createSections();

        // when
        Section section = sections.findSectionByUpStationId(1L);
        Long upStationId = section.getUpStationId();

        // then
        assertThat(upStationId).isEqualTo(1L);
    }

    @Test
    void findSectionByDownStationId() {
        // given
        Sections sections = createSections();

        // when
        Section section = sections.findSectionByDownStationId(2L);
        Long upStationId = section.getDownStationId();

        // then
        assertThat(upStationId).isEqualTo(2L);
    }

    @Test
    void sortedStationId() {
        // given
        Sections sections = createSections();

        // when
        List<Long> stationIds = sections.sortedStationId();
        List<Long> result = List.of(1L, 2L, 3L);

        // then
        assertThat(stationIds).containsExactlyInAnyOrderElementsOf(result);
    }

    @Test
    @DisplayName("구간이 한개만 존재할 경우, false를 반환한다.")
    void isSingleSection() {
        // given
        List<Section> inputSections = List.of(new Section(1L, 2L));
        Sections sections = new Sections(inputSections);

        // when
        boolean result = sections.canRemoveSection();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("구간이 한개만 비어있을 경우, false를 반환한다.")
    void isEmpty() {
        // given
        List<Section> inputSections = List.of();
        Sections sections = new Sections(inputSections);

        // when
        boolean result = sections.canRemoveSection();

        // then
        assertThat(result).isFalse();
    }

    Sections createSections() {
        List<Section> inputSections = List.of(new Section(1L, 2L), new Section(2L, 3L));
        return new Sections(inputSections);
    }
}
