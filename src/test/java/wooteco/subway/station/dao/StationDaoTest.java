package wooteco.subway.station.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    private static final Station STATION_1 = new Station("잠실역");
    private static final Station STATION_2 = new Station("압구정로데오역");

    @Autowired
    private StationDao stationDao;

    private Station station;
    private Station station2;

    @BeforeEach
    void setUp() {
        station = stationDao.save(STATION_1);
        station2 = stationDao.save(STATION_2);
    }

    @Test
    void save() {
        assertThat(station.getName()).isEqualTo(STATION_1.getName());
    }

    @Test
    void saveDuplicate() {
        assertThatThrownBy(() -> stationDao.save(STATION_1))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        List<Station> stations = stationDao.findAll();
        List<String> stationNames = stations.stream()
            .map(Station::getName)
            .collect(Collectors.toList());

        assertThat(stationNames).containsExactly(STATION_1.getName(), STATION_2.getName());
    }

    @Test
    void deleteById() {
        stationDao.deleteById(station.getId());

        assertThatThrownBy(() -> stationDao.findById(station.getId()))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void findAllByIds() {
        List<Station> stations = stationDao
            .findAllByIds(Arrays.asList(station.getId(), station2.getId()));
        List<String> stationNames = stations.stream()
            .map(Station::getName)
            .collect(Collectors.toList());

        assertThat(stationNames).containsExactly(STATION_1.getName(), STATION_2.getName());
    }
}
