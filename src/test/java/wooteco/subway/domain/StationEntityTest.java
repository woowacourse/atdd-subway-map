package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StationEntityTest {

    @ParameterizedTest
    @CsvSource(value = {"라:1", "라:30"}, delimiter = ':')
    @DisplayName("정상적인 길이의 이름으로 Station 을 생성한다.")
    void createStation(String name, int repeatCount) {
        //given
        String stationName = name.repeat(repeatCount);

        //when
        StationEntity station = new StationEntity(stationName);

        //then
        assertThat(station.getName()).isEqualTo(stationName);
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:31"}, delimiter = ':')
    @DisplayName("길이 범위를 벗어나는 이름으로 Station 을 생성할 경우 예외를 던진다.")
    void createStationWithInvalidLength(String name, int repeatCount) {
        //given
        String stationName = name.repeat(repeatCount);

        //when, then
        assertThatThrownBy(() -> new StationEntity(stationName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이름은 1~30 자 이내여야 합니다.");
    }

    @Test
    @DisplayName("이름을 null 값으로 Station 을 생성할 경우 예외를 던진다.")
    void createStationWithNull() {
        assertThatThrownBy(() -> new StationEntity(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("이름은 Null 일 수 없습니다.");
    }
}
