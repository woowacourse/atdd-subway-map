package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.station.exception.StationExistenceException;
import wooteco.subway.station.exception.StationNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StationServiceTest {
    private final String stationName = "잠실역";

    @Autowired
    private StationService stationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationRequest stationRequest = new StationRequest("잠실역");

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("역 삭제 테스트")
    public void deleteStation() {
        Station savedStation = stationService.createStation(stationRequest);
        assertThat(stationService.findAll().size()).isEqualTo(1);

        stationService.delete(savedStation.getId());
        assertThat(stationService.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않은 역 삭제 테스트")
    public void deleteNotExistingStation() {
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(StationNotFoundException.class);
    }
}