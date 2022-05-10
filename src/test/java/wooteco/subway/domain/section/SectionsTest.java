package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.station.Station;

class SectionsTest {

    @DisplayName("구간을 정렬한다.")
    @Test
    void orderSections() {
        Station station1 = new Station(1L, "역삼역");
        Station station2 = new Station(2L, "선릉역");
        Station station3 = new Station(3L, "강남역");
        Station station4 = new Station(4L, "삼성역");

        Sections sections = Sections.orderSections(station3, List.of(
                new Section(1L, station1, station2, 1),
                new Section(2L, station3, station1, 1),
                new Section(3L, station2, station4, 1)
        ));
        assertThat(sections.getStations()).containsExactly(station3, station1, station2, station4);
    }
}