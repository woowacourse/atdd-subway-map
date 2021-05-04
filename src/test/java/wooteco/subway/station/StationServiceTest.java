package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StationServiceTest {
    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        Station savedStation = stationService.createStation("서울역");
        assertEquals("서울역", savedStation.getName());
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        stationService.createStation("서울역");
        assertThatThrownBy(() -> stationService.createStation("서울역"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}