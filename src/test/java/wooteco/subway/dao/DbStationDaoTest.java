package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 테스트")
@JdbcTest
class DbStationDaoTest {
    
    private DbStationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new DbStationDao(jdbcTemplate);
    }
    
    @DisplayName("저장을 하고 리스트 및 단일 조회를 할 수 있다")
    @Test
    void can_save_find_findAll() {
        List<Station> stationsBeforeSave = stationDao.findAll();
        assertThat(stationsBeforeSave.size()).isEqualTo(0);

        Station station = new Station("선릉역");
        long savedStationId = stationDao.save(station);

        List<Station> stationsAfterSave = stationDao.findAll();
        assertThat(stationsAfterSave.size()).isEqualTo(1);

        Station foundStation = stationDao.findById(savedStationId).get();
        assertThat(foundStation).isEqualTo(new Station(savedStationId, "선릉역"));
    }

    @DisplayName("삭제를 할 수 있다")
    @Test
    void can_delete() {
        Station station = new Station("선릉역");
        long savedStationId = stationDao.save(station);

        stationDao.deleteById(savedStationId);

        List<Station> foundStations = stationDao.findAll();
        assertThat(foundStations.size()).isEqualTo(0);
    }

    @DisplayName("전체 삭제를 할 수 있다")
    @Test
    void can_deleteAll() {
        stationDao.save(new Station("노량진역"));
        stationDao.save(new Station("영등포역"));

        stationDao.deleteAll();

        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(0);
    }
}