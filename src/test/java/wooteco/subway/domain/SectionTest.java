package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.fixture.StationFixture.stationA;
import static wooteco.subway.fixture.StationFixture.stationB;
import static wooteco.subway.fixture.StationFixture.stationC;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행 지하철역과 하행 지하철역이 맞는지 검증 한다.")
    @Test
    void matchUpStationAndDownStation() {
        Section section = new Section(1L, 1L, stationA, stationB, 10);

        assertAll(
                () -> assertThat(section.matchUpStationAndDownStation(stationA, stationB)).isTrue(),
                () -> assertThat(section.matchUpStationAndDownStation(stationB, stationA)).isFalse(),
                () -> assertThat(section.matchUpStationAndDownStation(stationA, stationC)).isFalse()
        );
    }

    @DisplayName("구간의 거리에서 다른 구간이 추가될 수 있는 거리를 가지는 지 검증한다.")
    @Test
    void checkDistance() {
        Section section = new Section(1L, 1L, stationA, stationB, 10);

        assertAll(
                () -> assertDoesNotThrow(() -> section.checkDistance(9)),
                () -> assertThatThrownBy(() -> section.checkDistance(10))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간 사이의 거리가 너무 멉니다."),
                () -> assertThatThrownBy(() -> section.checkDistance(11))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간 사이의 거리가 너무 멉니다.")
        );
    }

    @DisplayName("상행 지하철역과 하행 지하철역이 구간에 포함되어 있는지 확인 한다.")
    @Test
    void hasStation() {
        Section section = new Section(1L, 1L, stationA, stationB, 10);

        assertAll(
                () -> assertThat(section.hasStation(stationA)).isTrue(),
                () -> assertThat(section.hasStation(stationB)).isTrue(),
                () -> assertThat(section.hasStation(stationC)).isFalse()
        );
    }
}
