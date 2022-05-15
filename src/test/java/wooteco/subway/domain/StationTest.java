package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @DisplayName("구간 리스트에 해당 지하철역이 등록되어 있는 지 확인한다. - 참")
    @Test
    void isResistedStationTrue() {
        final Station station1 = new Station(1L, "선릉역");
        final Station station2 = new Station(2L, "잠실역");
        final Station station3 = new Station(3L, "강남역");
        final Station station4 = new Station(4L, "역삼역");
        final List<Section> sections = List.of(
                new Section(1L, station1, station2, 10),
                new Section(2L, station3, station1, 10),
                new Section(3L, station3, station4, 5)
        );

        assertThat(station1.isResistedStation(sections)).isTrue();
    }

    @DisplayName("구간 리스트에 해당 지하철역이 등록되어 있는 지 확인한다. - 거짓")
    @Test
    void isResistedStationFalse() {
        final Station station1 = new Station(1L, "선릉역");
        final Station station2 = new Station(2L, "잠실역");
        final Station station3 = new Station(3L, "강남역");
        final Station station4 = new Station(4L, "역삼역");
        final List<Section> sections = List.of(
                new Section(1L, station1, station2, 10),
                new Section(2L, station3, station1, 10),
                new Section(3L, station3, station4, 5)
        );

        final Station station5 = new Station(5L, "삼성역");

        assertThat(station5.isResistedStation(sections)).isFalse();
    }
}