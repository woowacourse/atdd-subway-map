package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private static final Station STATION = new Station("강남역");
    
    private JdbcStationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        stationDao.save(STATION);

        Integer count = jdbcTemplate.queryForObject("select count(*) from STATION", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("해당 Id의 지하철역이 있다면 true를 반환한다.")
    @Test
    void existStationById() {
        long stationId = stationDao.save(STATION);

        assertThat(stationDao.existStationById(stationId)).isTrue();
    }

    @DisplayName("해당 이름의 지하철역이 있다면 true를 반환한다.")
    @Test
    void existStationByName() {
        stationDao.save(STATION);

        assertThat(stationDao.existStationByName("강남역")).isTrue();
    }

    @DisplayName("지하철역의 전체 목록을 조회한다.")
    @Test
    void findAll() {
        stationDao.save(STATION);
        stationDao.save(new Station("선릉역"));

        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        long stationId = stationDao.save(STATION);

        stationDao.delete(stationId);

        assertThat(stationDao.existStationById(stationId)).isFalse();
    }
}
