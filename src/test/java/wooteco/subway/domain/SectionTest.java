package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.fixture.StationFixture.AStation;
import static wooteco.subway.fixture.StationFixture.BStation;
import static wooteco.subway.fixture.StationFixture.CStation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행 지하철역과 하행 지하철역이 맞는지 검증 한다.")
    @Test
    void matchUpStationAndDownStation() {
        Section section = new Section(1L, 1L, AStation, BStation, 10);

        assertAll(
                () -> assertThat(section.matchUpStationAndDownStation(AStation, BStation)).isTrue(),
                () -> assertThat(section.matchUpStationAndDownStation(BStation, AStation)).isFalse(),
                () -> assertThat(section.matchUpStationAndDownStation(AStation, CStation)).isFalse()
        );
    }

    @DisplayName("구간의 거리에서 다른 구간이 추가될 수 있는 거리를 가지는 지 검증한다.")
    @Test
    void checkDistance() {
        Section section = new Section(1L, 1L, AStation, BStation, 10);

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
}
