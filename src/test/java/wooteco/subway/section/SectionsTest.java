package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    private Sections sections;

    @BeforeEach
    void setUp() {
        Station station1 = new Station(1L, "염창역");
        Station station2 = new Station(2L, "가양역");
        Station station3 = new Station(3L, "등촌역");
        Station station4 = new Station(4L, "증미역");
        Section section1 = new Section(1L, 1L, station2, station4, 10);
        Section section2 = new Section(2L, 1L, station4, station3, 10);
        Section section3 = new Section(3L, 1L, station3, station1, 10);
        sections = new Sections(Arrays.asList(section3, section1, section2));
    }

    @Test
    @DisplayName("Sections 내 Station 정렬 확인")
    public void sortedStations() {
        assertThat(sections.sortedStations())
                .containsExactly(
                        new Station(2L, "가양역"),
                        new Station(4L, "증미역"),
                        new Station(3L, "등촌역"),
                        new Station(1L, "염창역")
                );
    }

    @Test
    @DisplayName("종점역에 구간을 붙이는지 여부 확인")
    public void canAttachAfterEndStation() {
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(1L, 5L, 10));
        SectionDto sectionDto2 = SectionDto.of(1L, new SectionRequest(5L, 1L, 10));

        assertThat(sections.canAttachAfterEndStation(
                sectionDto.getUpStationId(), sectionDto.getDownStationId())).isTrue();
        assertThat(sections.canAttachAfterEndStation(
                sectionDto2.getUpStationId(), sectionDto2.getDownStationId())).isFalse();
    }
}
