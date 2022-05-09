package wooteco.subway.repository.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.StationDao;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(dataSource);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("강남역");
        Long stationId = stationDao.save(station);
        assertThat(stationId).isGreaterThan(0);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void findAll() {
        List<Station> stations = List.of(
                new Station("강남역"),
                new Station("역삼역"),
                new Station("선릉역")
        );
        stations.forEach(stationDao::save);
        List<Station> foundStations = stationDao.findAll();
        assertThat(foundStations).hasSize(3);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findById() {
        Long stationId = stationDao.save(new Station("강남역"));
        Optional<Station> station = stationDao.findById(stationId);

        assertAll(() -> {
            assertThat(station.isPresent()).isTrue();
            assertThat(station.get().getId()).isEqualTo(stationId);
            assertThat(station.get().getName()).isEqualTo("강남역");
        });
    }

    @DisplayName("존재하지 않는 지하철역을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<Station> station = stationDao.findById(1L);
        assertThat(station.isEmpty()).isTrue();
    }

    @DisplayName("해당 이름의 지하철역이 존재하는지 확인한다.")
    @Test
    void existsByName() {
        stationDao.save(new Station("강남역"));
        assertThat(stationDao.existsByName("강남역")).isTrue();
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void remove() {
        Long stationId = stationDao.save(new Station("강남역"));
        stationDao.remove(stationId);
        assertThat(stationDao.findAll()).isEmpty();
    }
}
