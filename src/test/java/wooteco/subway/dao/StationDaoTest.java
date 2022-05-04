package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("역을 저장한다.")
    void save() {
        final Station station = new Station("한성대입구역");

        final Long id = stationDao.save(station);

        final String actual = stationDao.find(id).getName();
        assertThat(actual).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("역을 조회한다.")
    void find() {
        final Station station = new Station("한성대입구역");
        final long id = stationDao.save(station);

        final Station foundStation = stationDao.find(id);

        assertThat(foundStation.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        final Station station1 = new Station("한성대입구역");
        final Station station2 = new Station("신대방역");
        stationDao.save(station1);
        stationDao.save(station2);

        final List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void delete() {
        final Station station = new Station("한성대입구역");
        final Long id = stationDao.save(station);

        stationDao.delete(id);

        final int actual = stationDao.findAll().size();
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("역 이름의 수를 조회한다.")
    void count() {
        final Station station = new Station("한성대입구역");
        stationDao.save(station);

        final int count = stationDao.countByName(station.getName());

        assertThat(count).isEqualTo(1);
    }
}
