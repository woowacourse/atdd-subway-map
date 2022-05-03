package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @Test
    @DisplayName("객체를 생성한다.")
    void createStation() {
        final String stationName = "강남역";

        final Station station = new Station(stationName);

        assertThat(station.getName()).isEqualTo(stationName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("역 이름이 공백인 경우 예외를 발생한다.")
    void createEmptyName(final String name) {
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 이름은 공백일 수 없습니다.");
    }
}
