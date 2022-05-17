package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.station.Station;

class SectionsSorterTest {

    private static final int STANDARD_DISTANCE = 5;
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 선릉역 = new Station(3L, "선릉역");
    private static final Station 삼성역 = new Station(4L, "삼성역");

    private static final SectionsSorter SECTIONS_SORTER = new SectionsSorter();

    @DisplayName("구간을 정렬한다.")
    @Test
    void create() {
        Section section1 = new Section(1L, 역삼역, 선릉역, STANDARD_DISTANCE);
        Section section2 = new Section(2L, 강남역, 역삼역, STANDARD_DISTANCE);
        List<Section> sections = List.of(section1, section2);

        List<Section> expected = List.of(section2, section1);
        List<Section> actual = SECTIONS_SORTER.create(sections);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("구간이 하나도 없다.")
    @Test
    void createWithEmptySections() {
        assertThatThrownBy(() -> SECTIONS_SORTER.create(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철구간은 하나 이상이어야 합니다.");
    }

    @DisplayName("정렬되지 않은 구간이 존재한다.")
    @Test
    void createWithNonConnectedSections() {
        List<Section> sections = List.of(
                new Section(1L, 역삼역, 선릉역, STANDARD_DISTANCE),
                new Section(2L, 역삼역, 강남역, STANDARD_DISTANCE));
        assertThatThrownBy(() -> SECTIONS_SORTER.create(sections))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("정렬되지 않은 구간이 존재합니다.");
    }
}