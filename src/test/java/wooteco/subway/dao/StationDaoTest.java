package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDaoImpl stationDao;

    @BeforeEach
    void beforeEach() {
        stationDao = new StationDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void insert() {
        String name = "강남역";
        Station station = stationDao.insert(new Station(name));

        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("이미 존재하는 이름의 지하철 역인지 확인한다.")
    void existByName() {
        Station station = stationDao.insert(new Station("강남역"));
        Boolean actual = stationDao.existByName(new Station("강남역"));

        assertThat(actual).isEqualTo(true);
    }

    @Test
    @DisplayName("지하철 역들을 조회할 수 있다.")
    void findAll() {
        stationDao.insert(new Station("강남역"));
        stationDao.insert(new Station("역삼역"));

        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void delete() {
        Station station = stationDao.insert(new Station("강남역"));
        stationDao.delete(station.getId());

        List<Station> stations = stationDao.findAll();
        assertThat(stations).isEmpty();
    }
}
