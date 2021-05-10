package wooteco.subway.station.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.station.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCStationDaoTest {

    private final JDBCStationDao jdbcStationDao;

    public JDBCStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcStationDao = new JDBCStationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("역 추가 테스트")
    void save() {
        Station station = new Station("강남역");

        Station savedStation = jdbcStationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("역 조회 테스트")
    void findAll() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        jdbcStationDao.save(station1);
        jdbcStationDao.save(station2);

        List<Station> stations = jdbcStationDao.findAll();

        assertThat(stations)
                .hasSize(2)
                .containsExactly(new Station(1L, "강남역"), new Station(2L, "역삼역"));
    }

    @Test
    @DisplayName("아이디로 역 조회 테스트")
    void findById() {
        Station station1 = new Station("강남역");
        Station savedStation = jdbcStationDao.save(station1);

        Station findByIdStation = jdbcStationDao.findById(savedStation.getId()).orElseThrow(NotFoundStationException::new);
        assertThat(savedStation.getName()).isEqualTo(findByIdStation.getName());
    }

    @Test
    @DisplayName("역 삭제 테스트")
    void delete() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        jdbcStationDao.save(station1);
        Station savedStation = jdbcStationDao.save(station2);

        jdbcStationDao.delete(savedStation.getId());
        List<Station> findStations = jdbcStationDao.findAll();

        assertThat(findStations).hasSize(1);
    }
}