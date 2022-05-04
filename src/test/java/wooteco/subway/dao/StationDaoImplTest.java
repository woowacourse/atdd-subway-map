package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDaoImpl(jdbcTemplate);

        List<Station> stations = stationDao.findAll();
        List<Long> stationIds = stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        Station station = new Station("범고래");

        // when
        Station result = stationDao.save(station);

        // then
        assertThat(station).isEqualTo(result);
    }

    @Test
    void findAll() {
        // given
        Station station1 = stationDao.save(new Station("범고래"));
        Station station2 = stationDao.save(new Station("애쉬"));

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations)
            .hasSize(2)
            .contains(station1, station2);
    }

    @Test
    void validateDuplication() {
        // given
        Station station1 = new Station("범고래");
        Station station2 = new Station("범고래");

        // when
        stationDao.save(station1);

        // then
        assertThatThrownBy(() -> stationDao.save(station2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void delete() {
        // given
        Station station = stationDao.save(new Station("범고래"));

        // when
        stationDao.deleteById(station.getId());
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations)
            .hasSize(0)
            .doesNotContain(station);
    }
}
