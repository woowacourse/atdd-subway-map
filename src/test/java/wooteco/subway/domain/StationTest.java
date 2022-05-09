package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StationTest {

    @ParameterizedTest
    @CsvSource(value = {"라:1", "라:30"}, delimiter = ':')
    @DisplayName("정상적인 길이의 이름으로 Station 을 생성한다.")
    void createStation(String name, int repeatCount) {
        //given
        String stationName = name.repeat(repeatCount);

        //when
        Station station = new Station(stationName);

        //then
        assertThat(station.getName()).isEqualTo(stationName);
    }
}
