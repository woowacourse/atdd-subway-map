package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StationServiceTest {
    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        StationService stationService = new StationService();
        Station savedStation = stationService.createStation("서울역");
        assertEquals("서울역", savedStation.getName());
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        StationService stationService = new StationService();
        stationService.createStation("서울역");
        assertThatThrownBy(() -> stationService.createStation("서울역"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}