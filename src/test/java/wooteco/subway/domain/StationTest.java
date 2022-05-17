package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @DisplayName("같은 이름을 가지고 있는지 비교한다.")
    @Test
    void hasSameName_true() {
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station("선릉역");

        assertThat(station1.hasSameName(station2)).isTrue();
    }

    @DisplayName("다른 이름을 가지고 있는지 비교한다.")
    @Test
    void hasSameName_false() {
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station("강남역");

        assertThat(station1.hasSameName(station2)).isFalse();
    }

    @DisplayName("같은 id인지 비교한다.")
    @Test
    void isSameId_true() {
        Station station1 = new Station(1L, "선릉역");

        assertThat(station1.isSameId(1L)).isTrue();
    }

    @DisplayName("다른 id인지 비교한다.")
    @Test
    void isSameId_false() {
        Station station1 = new Station(1L, "선릉역");

        assertThat(station1.isSameId(2L)).isFalse();
    }


}