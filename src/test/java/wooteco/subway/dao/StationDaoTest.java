package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void beforeEach() {
        stationDao = new StationDao(dataSource);
    }

    @DisplayName("새 지하철역을 저장한다.")
    @Test
    void save() {
        Station testStation = new Station(null, "hi");
        Station result = stationDao.save(testStation);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("hi")
        );
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회한다.")
    @Test
    void findByName() {
        Station test = new Station(null, "test");
        stationDao.save(test);
        Station result = stationDao.findByName("test").orElse(null);
        Optional<Station> result2 = stationDao.findByName("test2");

        assertAll(
                () -> assertThat(result.getName()).isEqualTo("test"),
                () -> assertThat(result2).isEmpty()
        );
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회할 때 없으면 empty를 반환한다.")
    @Test
    void findByName_noSuchName() {
        Optional<Station> result = stationDao.findByName("test2");

        assertThat(result).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        Station test1 = new Station(null, "test1");
        Station test2 = new Station(null, "test2");
        Station savedTestStation1 = stationDao.save(test1);
        Station savedTestStation2 = stationDao.save(test2);

        List<Station> stations = stationDao.findAll();

        assertAll(
                () -> assertThat(stations.size()).isEqualTo(2),
                () -> assertThat(stations.get(0).getId()).isEqualTo(savedTestStation1.getId()),
                () -> assertThat(stations.get(0).getName()).isEqualTo("test1"),
                () -> assertThat(stations.get(1).getId()).isEqualTo(savedTestStation2.getId()),
                () -> assertThat(stations.get(1).getName()).isEqualTo("test2")
        );
    }

    @DisplayName("지하철역 id를 이용해 지하철역을 조회한다.")
    @Test
    void findById() {
        Station test = new Station(null, "test");
        Station savedStation = stationDao.save(test);
        Station result = stationDao.findById(savedStation.getId()).orElse(null);

        assertAll(
                () -> assertThat(result.getId()).isEqualTo(savedStation.getId()),
                () -> assertThat(result.getName()).isEqualTo("test")
        );
    }

    @DisplayName("지하철역 id를 이용해 지하철역을 조회할 때 없으면 empty를 반환한다.")
    @Test
    void findById_noSuchId() {
        Optional<Station> result = stationDao.findById(1000L);
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
