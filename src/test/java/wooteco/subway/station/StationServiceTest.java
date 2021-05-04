package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        StationDao.clear();
        stationService = new StationService();
    }

    @DisplayName("역 추가 기능")
    @Test
    void createStation() {
        final String name = "잠실역";
        stationService.createStation(name);
        final Station station = stationService.findByName(name);
        assertThat(station.getName()).isEqualTo(name);
    }

    @DisplayName("역 조회 기능")
    @Test
    void findStations() {

    }

    @DisplayName("역 제거 기능")
    @Test
    void deleteStation() {

    }
}
