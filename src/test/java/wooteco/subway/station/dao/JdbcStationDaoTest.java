package wooteco.subway.station.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.exception.notfoundexception.NotFoundStationException;
import wooteco.subway.station.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JdbcStationDaoTest {

    private final JdbcStationDao jdbcStationDao;

    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcStationDao = new JdbcStationDao(jdbcTemplate);
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
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Station 저장된강남역 = jdbcStationDao.save(강남역);
        Station 저장된역삼역 = jdbcStationDao.save(역삼역);

        List<Station> stations = jdbcStationDao.findAll();

        assertThat(stations)
                .hasSize(2)
                .containsExactly(저장된강남역, 저장된역삼역);
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