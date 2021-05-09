package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.station.exception.StationExistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(savedStation.getName()).isEqualTo(stationName);
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        stationService.createStation(stationRequest);
        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(StationExistenceException.class);
    }
}