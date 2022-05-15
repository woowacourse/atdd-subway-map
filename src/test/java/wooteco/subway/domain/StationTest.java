package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    private static final String EXCESS_MAX_LENGTH_STRING = "-".repeat(256);

    @DisplayName("지하철역의 이름은 공백일 수 없다.")
    @Test
    public void blankNameTest() {
        assertThatThrownBy(() -> new Station(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철역의 이름은 255보다 클 수 없다.")
    @Test
    public void stationNameLengthTest() {
        assertThatThrownBy(() -> new Station(EXCESS_MAX_LENGTH_STRING))
                .isInstanceOf(IllegalArgumentException.class);
    }
}