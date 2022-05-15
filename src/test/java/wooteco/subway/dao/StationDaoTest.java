package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@Sql("/sql/schema-test.sql")
@JdbcTest
class StationDaoTest {

    private static final String FIRST_STATION_NAME = "신설동역";
    private static final String SECOND_STATION_NAME = "성수역";

    private static final Station FIRST_STATION = new Station(null, FIRST_STATION_NAME);
    private static final Station SECOND_STATION = new Station(null, SECOND_STATION_NAME);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void beforeEach() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("새 지하철역을 저장한다.")
    @Test
    void save() {
        Station result = stationDao.save(FIRST_STATION);
        assertAll(
            () -> assertNotNull(result.getId()),
            () -> assertThat(result.getName()).isEqualTo(FIRST_STATION_NAME)
        );
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회한다.")
    @Test
    void findByName() {
        stationDao.save(FIRST_STATION);
        Station result = stationDao.findByName(FIRST_STATION_NAME).orElse(null);

        assertThat(result.getName()).isEqualTo(FIRST_STATION_NAME);
    }

    @DisplayName("존재하지 않는 지하철역 이름을 이용해 지하철역을 조회하면 empty를 반환한다.")
    @Test
    void findByName_empty() {
        stationDao.save(FIRST_STATION);

        Optional<Station> result = stationDao.findByName(SECOND_STATION_NAME);

        assertThat(result).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        stationDao.save(FIRST_STATION);
        stationDao.save(SECOND_STATION);

        List<Station> stations = stationDao.findAll();

        assertAll(
            () -> assertThat(stations.size()).isEqualTo(2),
            () -> assertThat(stations.get(0).getId()).isEqualTo(1),
            () -> assertThat(stations.get(0).getName()).isEqualTo(FIRST_STATION_NAME),
            () -> assertThat(stations.get(1).getId()).isEqualTo(2),
            () -> assertThat(stations.get(1).getName()).isEqualTo(SECOND_STATION_NAME)
        );
    }

    @DisplayName("지하철역 id를 이용해 지하철역을 조회한다.")
    @Test
    void findById() {
        stationDao.save(FIRST_STATION);
        Station result = stationDao.findById(1L).orElse(null);

        assertThat(result.getName()).isEqualTo(FIRST_STATION_NAME);
    }

    @DisplayName("존재하지 않는 지하철역 id를 이용해 지하철역을 조회하면 empty를 반환한다.")
    @Test
    void findById_empty() {
        stationDao.save(FIRST_STATION);

        Optional<Station> result = stationDao.findById(2L);

        assertThat(result).isEmpty();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Station savedTest = stationDao.save(FIRST_STATION);
        stationDao.save(SECOND_STATION);

        stationDao.delete(savedTest);
        List<Station> result = stationDao.findAll();

        assertThat(result.size()).isEqualTo(1);
    }
}
