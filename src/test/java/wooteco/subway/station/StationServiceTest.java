package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;
import wooteco.subway.station.service.StationService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@Sql("/init-station.sql")
@SpringBootTest
class StationServiceTest {
    private static final StationRequest stationRequest = new StationRequest("잠실역");

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        Station savedStation = stationService.createStation(stationRequest);
        assertEquals(stationRequest.getName(), savedStation.getName());
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        stationService.createStation(stationRequest);
        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(StationException.class)
                .hasMessage(StationError.ALREADY_EXIST_STATION_NAME.getMessage());
    }
}
