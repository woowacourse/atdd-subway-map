package wooteco.subway.dao.station;

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
        long savedStationId = stationDao.save(new Station("배카라"));

        assertThat(savedStationId).isNotNull();
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        stationDao.save(new Station("오리"));
        stationDao.save(new Station("배카라"));

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("id로 Station을 조회한다.")
    void findById() {
        long savedStationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.findById(savedStationId)).isNotNull();
    }

    @Test
    @DisplayName("Station 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        String name = "배카라";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName(name)).isTrue();
    }

    @Test
    @DisplayName("id를 가진 Station이 존재하는지 확인할 수 있다.")
    void existById() {
        long savedStationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.existById(savedStationId)).isTrue();
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        long stationId = stationDao.save(new Station("오리"));

        assertThat(stationDao.delete(stationId)).isEqualTo(1);
    }
}
