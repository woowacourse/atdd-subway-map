package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.STATION_4;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("역의 노선들을 순서대로 반환한다.")
    @Test
    void calculateStations() {
        SectionWithStation sectionWithStation = new SectionWithStation(1L, 1L, STATION, STATION_2, 5);
        SectionWithStation sectionWithStation2 = new SectionWithStation(3L, 1L, STATION_2, STATION_3, 10);
        SectionWithStation sectionWithStation3 = new SectionWithStation(2L, 1L, STATION_3, STATION_4, 6);
        Sections sections = new Sections(List.of(sectionWithStation2, sectionWithStation, sectionWithStation3));
        assertThat(sections.calculateStations())
                .containsOnly(STATION, STATION_2, STATION_3, STATION_4);
    }
}
