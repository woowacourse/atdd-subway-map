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

        List<Station> stationEntities = stationDao.findAll();
        List<Long> stationIds = stationEntities.stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        Station Station = new Station("범고래");

        // when
        Station result = stationDao.save(Station);

        // then
        assertThat(Station.getName()).isEqualTo(result.getName());
    }

    @Test
    void findAll() {
        // given
        Station station1 = stationDao.save(new Station("범고래"));
        Station station2 = stationDao.save(new Station("애쉬"));

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).containsExactly(station1, station2);
    }

    @Test
    void validateDuplication() {
        // given
        Station Station1 = new Station("범고래");
        Station Station2 = new Station("범고래");

        // when
        stationDao.save(Station1);

        // then
        assertThatThrownBy(() -> stationDao.save(Station2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void delete() {
        // given
        Station Station = stationDao.save(new Station("범고래"));

        // when
        stationDao.deleteById(Station.getId());
        List<Station> stationEntities = stationDao.findAll();

        // then
        assertThat(stationEntities).doesNotContain(Station);
    }
}
