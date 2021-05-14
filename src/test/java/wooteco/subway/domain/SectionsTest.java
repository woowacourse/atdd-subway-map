package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        //given
        Section section = Section.of(Station.from("석촌역"), Station.from("강남역"), 20);
        Sections sections = Sections.from(section);

        //then
        assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("구간을 정렬한 후 역을 반환한다.")
    @Test
    void sortedStations() {
        //given
        Station 석촌역 = Station.of(1L, "석촌역");
        Station 강남역 = Station.of(2L, "강남역");
        Station 잠실역 = Station.of(3L, "잠실역");
        Station 수서역 = Station.of(4L, "수서역");
        Station 몽촌토성역 = Station.of(5L, "몽촌토성역");
        Station 삼성역 = Station.of(6L, "삼성역");

        Section section1 = Section.of(석촌역, 강남역, 15);
        Section section2 = Section.of(삼성역, 잠실역, 10);
        Section section3 = Section.of(수서역, 몽촌토성역, 10);
        Section section4 = Section.of(잠실역, 수서역, 5);
        Section section5 = Section.of(강남역, 삼성역, 5);

        Sections sections = Sections.from(Arrays.asList(section1, section2, section3, section4, section5));

        //then
        assertThat(sections.asStations().get(0)).isEqualTo(석촌역);
        assertThat(sections.asStations().get(1)).isEqualTo(강남역);
        assertThat(sections.asStations().get(2)).isEqualTo(삼성역);
        assertThat(sections.asStations().get(3)).isEqualTo(잠실역);
        assertThat(sections.asStations().get(4)).isEqualTo(수서역);
        assertThat(sections.asStations().get(5)).isEqualTo(몽촌토성역);
    }
}
