package wooteco.subway.infrastructure.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.station.Station;
import wooteco.subway.infrastructure.station.StationDao;
import wooteco.util.StationFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql("classpath:/station/stationQueryInit.sql")
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
    void save() {
        stationDao.save(StationFactory.create("잠실역"));

        Station station = stationDao.findById(1L);

        assertThat(station).isEqualTo(StationFactory.create(1L, "잠실역"));
    }

    @Test
    void findAll() {
        stationDao.save(StationFactory.create("잠실역1"));
        stationDao.save(StationFactory.create("잠실역2"));
        stationDao.save(StationFactory.create("잠실역3"));

        List<Station> stations = stationDao.findAll();

        assertThat(stations).isEqualTo(Arrays.asList(
                StationFactory.create(1L, "잠실역1"),
                StationFactory.create(2L, "잠실역2"),
                StationFactory.create(3L, "잠실역3")
        ));
    }

    @Test
    void findById() {
        stationDao.save(StationFactory.create("잠실역"));

        Station station = stationDao.findById(1L);

        assertThat(station).isEqualTo(StationFactory.create(1L, "잠실역"));
    }

    @Test
    void delete() {
        stationDao.save(StationFactory.create("dummy"));

        stationDao.delete(1L);

        assertThatThrownBy(() -> stationDao.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}