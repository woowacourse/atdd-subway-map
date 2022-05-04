package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.dao.StationDao.DUPLICATE_EXCEPTION_MESSAGE;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
    void beforeEach() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    void save() {
        String name = "강남역";

        Station station = stationDao.save(new Station(name));
        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    void saveDuplicate() {
        String name = "강남역";
        stationDao.save(new Station(name));

        assertThatThrownBy(() -> stationDao.save(new Station(name)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    void findAll() {
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("역삼역"));

        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    void delete() {
        Station station = stationDao.save(new Station("강남역"));
        stationDao.delete(station.getId());

        List<Station> stations = stationDao.findAll();
        assertThat(stations).isEmpty();
    }
}
