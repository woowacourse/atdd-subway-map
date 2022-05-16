package wooteco.subway.dao.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station 추가")
    void save() {
        Station station = stationDao.create(new Station("선릉역"));

        assertThat(station.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("Station 목록 조회")
    void findAll() {
        Station station1 = stationDao.create(new Station("선릉역"));
        Station station2 = stationDao.create(new Station("잠실역"));
        List<Station> stations = stationDao.findAll();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations.get(0)).isEqualTo(station1),
                () -> assertThat(stations.get(1)).isEqualTo(station2)
        );
    }

    @Test
    @DisplayName("특정 Station 조회")
    void findByName() {
        Station station1 = stationDao.create(new Station("선릉역"));
        Station station2 = stationDao.findByName("선릉역");

        assertThat(station1).isEqualTo(station2);
    }

    @Test
    @DisplayName("특정 Station 삭제")
    void deleteById() {
        Station station = stationDao.create(new Station("선릉역"));
        stationDao.deleteById(station.getId());
        List<Station> stations = stationDao.findAll();

        assertThat(stations).doesNotContain(station);
    }
}