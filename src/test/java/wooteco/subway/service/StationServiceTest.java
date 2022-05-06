package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@JdbcTest
class StationServiceTest {

    private StationService stationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        stationService = new StationService(stationDao);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("hunch");
        stationService.save(station);
        assertThatThrownBy(() -> stationService.save(station))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 해당 이름의 역이 있습니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findAll() {
        Station station = new Station("강남역");
        Station station1 = new Station("선릉역");
        stationService.save(station);
        stationService.save(station1);

        assertThat(stationService.findAll())
            .hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("강남역");

        Station newStation = stationService.save(station);
        stationService.delete(newStation.getId());

        List<Station> stations = stationService.findAll();

        assertThat(stations).hasSize(0);
    }
}
