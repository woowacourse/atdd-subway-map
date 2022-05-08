package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("같은 이름을 갖는 station인지 확인한다.")
    void isSameName() {
        Station station = new Station("배카라");
        assertThat(station.isSameName("배카라")).isTrue();
    }

    @Test
    @DisplayName("다른 이름을 갖는 station인지 확인한다.")
    void isNotSameName() {
        Station station = new Station("배카라");
        assertThat(station.isSameName("오리")).isFalse();
    }
}