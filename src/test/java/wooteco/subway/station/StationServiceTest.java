package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StationServiceTest {
    private final String stationName = "잠실역";

    @Autowired
    private StationService stationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationRequest stationRequest;

    @BeforeEach
    void setUp() {
        stationRequest = new StationRequest("잠실역");
        jdbcTemplate.execute("truncate table STATION");
    }

    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        Station savedStation = stationService.createStation(stationRequest);
        assertEquals(stationName, savedStation.getName());
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        stationService.createStation(stationRequest);
        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(StationExistenceException.class);
    }
}