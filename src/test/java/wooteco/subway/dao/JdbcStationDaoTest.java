package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcStationDaoTest {

    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station을 저장할 수 있다.")
    void save() {
        Station station = new Station("배카라");
        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        stationDao.save(new Station("오리"));
        stationDao.save(new Station("배카라"));

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.save(new Station("오리"));
        Long stationId = station.getId();

        assertThat(stationDao.delete(stationId)).isEqualTo(1);
    }
}
