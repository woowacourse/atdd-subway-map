package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

class SectionTest {

    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = new Station("잠실역");
        downStation = new Station("잠실새내역");
    }

    @DisplayName("거리는 0 혹은 마이너스가 될 수 없다.")
    @Test
    void distance() {
        // given, when
        int minusDistance = -10;
        int zeroDistance = 0;


        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Section(upStation, downStation, minusDistance))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(upStation, downStation, zeroDistance))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("상행역과 하행역 하나라도 null이 될 수 없다.")
    @Test
    void station_not_null() {
        // given, when
        int distance = 10;

        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Section(upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }
}