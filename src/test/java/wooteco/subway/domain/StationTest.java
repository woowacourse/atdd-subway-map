package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.fixture.StationFixture.stationA;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @DisplayName("지하철역의 id가 같은지 확인한다.")
    @Test
    void isSameId() {
        Station station = stationA;

        assertThat(station.isSameId(stationA.getId())).isTrue();
    }
}
