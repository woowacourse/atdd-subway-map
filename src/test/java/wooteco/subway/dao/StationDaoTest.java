package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

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

@Sql("/schema-test.sql")
@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void beforEach() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("새 지하철역을 저장한다.")
    @Test
    void save() {
        Station testStation = new Station(null, "hi");
        Station result = stationDao.save(testStation);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("hi");
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회한다.")
    @Test
    void findByName() {
        Station test = new Station(null, "test");
        stationDao.save(test);
        Station result = stationDao.findByName("test").orElse(null);
        Optional<Station> result2 = stationDao.findByName("test2");

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result2).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        Station test1 = new Station(null, "test1");
        Station test2 = new Station(null, "test2");
        stationDao.save(test1);
        stationDao.save(test2);

        List<Station> stations = stationDao.findAll();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.get(0).getId()).isEqualTo(1);
        assertThat(stations.get(0).getName()).isEqualTo("test1");
        assertThat(stations.get(1).getId()).isEqualTo(2);
        assertThat(stations.get(1).getName()).isEqualTo("test2");
    }

    @DisplayName("지하철역 id를 이용해 지하철역을 조회한다.")
    @Test
    void findById() {
        Station test = new Station(null, "test");
        stationDao.save(test);
        Station result = stationDao.findById(1L).orElse(null);
        Optional<Station> result2 = stationDao.findById(2L);

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result2).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 제거한다.")
    @Test
    void deleteAll() {
        Station test = new Station(null, "test1");
        stationDao.save(test);
        Station test2 = new Station(null, "test2");
        stationDao.save(test2);

        stationDao.deleteAll();
        List<Station> result = stationDao.findAll();
        assertThat(result).isEmpty();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Station test = new Station(null, "test1");
        Station savedTest = stationDao.save(test);
        Station test2 = new Station(null, "test2");
        stationDao.save(test2);

        stationDao.delete(savedTest);

        List<Station> result = stationDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }
}
