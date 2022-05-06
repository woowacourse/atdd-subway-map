package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장하고 찾는다.")
    @Test
    void saveAndFind() {
        Station station = new Station("강남역");
        stationDao.save(station);
        assertThat(stationDao.findByName("강남역").getName())
            .isEqualTo("강남역");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findAll() {
        Station station = new Station("강남역");
        Station station1 = new Station("선릉역");
        stationDao.save(station);
        stationDao.save(station1);

        assertThat(stationDao.findAll())
            .hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("강남역");

        Station savedStation = stationDao.save(station);
        stationDao.delete(savedStation.getId());

        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(0);
    }
}
