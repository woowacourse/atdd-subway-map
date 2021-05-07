package wooteco.subway.station.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.station.Station;

@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    private Station station;

    @BeforeEach
    void setUp() {
        station = stationDao.save(new Station("잠실역"));
    }

    @Test
    void save() {
        assertThat(station).isEqualTo(new Station("잠실역"));
    }

    @Test
    void saveDuplicate() {
        assertThatThrownBy(() -> stationDao.save(new Station("잠실역")))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        List<Station> stations = stationDao.findAll();

        assertThat(stations).containsExactly(new Station("잠실역"));
    }

    @Test
    void deleteById() {
        stationDao.deleteById(station.getId());

        assertThatThrownBy(() -> stationDao.findById(station.getId()))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
