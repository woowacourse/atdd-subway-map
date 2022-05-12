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

        assertThat(stationDao.findAll()).hasSize(6);
    }

    @Test
    @DisplayName("Station 이름이 존재하는 경우 확인한다.")
    void existByName() {
        String name = "배카라";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName(name)).isTrue();
    }

    @Test
    @DisplayName("Station 이름이 존재하지 않는 경우 확인한다.")
    void nonExistByName() {
        String name = "배카라";
        stationDao.save(new Station(name));

        assertThat(stationDao.existByName("오리")).isFalse();
    }

    @Test
    @DisplayName("Station을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.save(new Station("오리"));
        stationDao.delete(station.getId());
        assertThat(stationDao.existById(station.getId())).isFalse();
    }

    @Test
    @DisplayName("id를 통해 Station이 존재하는 지 확인한다.")
    void existById() {
        Station station = stationDao.save(new Station("배카라"));
        assertThat(stationDao.existById(station.getId())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id를 통해 Station이 존재하는 지 확인한다.")
    void existByInvalidId() {
        Station station = stationDao.save(new Station("배카라"));
        assertThat(stationDao.existById(station.getId() + 1)).isFalse();
    }
}
