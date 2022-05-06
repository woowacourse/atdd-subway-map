package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("강남역");
        Long stationId = stationDao.save(station);
        assertThat(stationId).isGreaterThan(0);
    }

    @DisplayName("지하철 역 목록을 조회한다.")
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

    @DisplayName("id로 지하철 역을 조회한다.")
    @Test
    void findById() {
        Long stationId = stationDao.save(new Station("강남역"));
        Station station = stationDao.findById(stationId);

        assertThat(station.getId()).isEqualTo(stationId);
        assertThat(station.getName()).isEqualTo("강남역");
    }

    @DisplayName("없는 지하철 역을 조회하면 예외가 발생한다.")
    @Test
    void findByIdException() {
        assertThatThrownBy(() -> stationDao.findById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id에 맞는 지하철 역이 없습니다.");
    }

    @DisplayName("해당 이름의 지하철 역이 존재하는지 확인한다.")
    @Test
    void existsByName() {
        stationDao.save(new Station("강남역"));
        assertThat(stationDao.existsByName("강남역")).isTrue();
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void remove() {
        Long stationId = stationDao.save(new Station("강남역"));
        stationDao.remove(stationId);
        List<Station> stations = stationDao.findAll();

        assertThat(stations).isEmpty();
    }
}