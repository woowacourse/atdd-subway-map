package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    private static final String EXCESS_MAX_LENGTH_STRING = "-".repeat(256);

    @DisplayName("지하철역의 이름이 공백인지를 검사한다.")
    @Test
    public void blankNameTest() {
        // given & when & then
        assertThatThrownBy(() -> new Station(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철역은 255보다 크거나 같을 수 없다.")
    @Test
    public void stationNameLengthTest() {
        // given & when & then
        assertThatThrownBy(() -> new Station(EXCESS_MAX_LENGTH_STRING))
                .isInstanceOf(IllegalArgumentException.class);
    }
}