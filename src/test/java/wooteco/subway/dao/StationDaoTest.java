package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationDaoTest {

    @BeforeEach
    void beforEach() {
        StationDao.deleteAll();
    }

    @DisplayName("새 지하철역을 저장한다.")
    @Test
    void save() {
        Station testStation = new Station(null, "hi");
        Station result = StationDao.save(testStation);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("hi");
    }

    @DisplayName("지하철역 이름을 이용해 지하철역을 조회한다.")
    @Test
    void findByName() {
        Station test = new Station(null, "test");
        StationDao.save(test);
        Station result = StationDao.findByName("test").orElse(null);
        Optional<Station> result2 = StationDao.findByName("test2");

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result2).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        Station test1 = new Station(null, "test1");
        Station test2 = new Station(null, "test2");
        StationDao.save(test1);
        StationDao.save(test2);

        List<Station> stations = StationDao.findAll();

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
        StationDao.save(test);
        Station result = StationDao.findById(1L).orElse(null);
        Optional<Station> result2 = StationDao.findById(2L);

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result2).isEmpty();
    }

    @DisplayName("저장된 모든 지하철역을 제거한다.")
    @Test
    void deleteAll() {
        Station test = new Station(null, "test1");
        StationDao.save(test);
        Station test2 = new Station(null, "test2");
        StationDao.save(test2);

        StationDao.deleteAll();
        List<Station> result = StationDao.findAll();
        assertThat(result).isEmpty();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Station test = new Station(null, "test1");
        Station savedTest = StationDao.save(test);
        Station test2 = new Station(null, "test2");
        StationDao.save(test2);

        StationDao.delete(savedTest);

        List<Station> result = StationDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }
}
