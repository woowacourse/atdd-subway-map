package wooteco.subway.domain.station;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("지하철역")
class StationTest {

    private static final Long STATION_ID = 1L;
    private static final String STATION_NAME = "강남역";

    private Station station;

    @BeforeEach
    void setUp() {
        this.station = new Station(STATION_ID, STATION_NAME);
    }

    @DisplayName("식별자를 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,1,true", "1,2,false"})
    void equals(Long id1, Long id2, boolean expected) {
        Station thisStation = new Station(id1, STATION_NAME);
        Station otherStation = new Station(id2, STATION_NAME);

        boolean actual = thisStation.equals(otherStation);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("식별자를 반환한다.")
    @Test
    void getId() {
        Long id = station.getId();
        assertThat(id).isEqualTo(STATION_ID);
    }

    @DisplayName("이름을 반환한다.")
    @Test
    void getName() {
        String name = station.getName();
        assertThat(name).isEqualTo(STATION_NAME);
    }
}
