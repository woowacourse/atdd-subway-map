package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.EqualsUpStationAndDownStationSectionException;

class SectionTest {

    @Test
    @DisplayName("같은 역으로 구간 생성시 예외가 발생한다.")
    void createEqualsStations() {
        final Station station = Station.createWithoutId("강남역");

        assertThatThrownBy(() -> Section.createWithoutId(station, station, 10))
                .isInstanceOf(EqualsUpStationAndDownStationSectionException.class);
    }
}