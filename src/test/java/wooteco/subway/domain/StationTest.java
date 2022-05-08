package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.constant.BlankArgumentException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    @DisplayName("지하철 역 이름에 빈 문자열을 저장할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveEmptyName(String stationName) {
        assertThatThrownBy(() -> new Station(stationName))
            .isInstanceOf(BlankArgumentException.class);
    }
}