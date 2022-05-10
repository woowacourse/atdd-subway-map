package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private StationDao stationDao;
    private final Station station = new Station("선릉역");

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 역을 저장한다.")
    void save() {
        // given
        // when
        Long savedId = stationDao.save(station);

        // then
        assertThat(savedId).isPositive();
    }

    @Test
    @DisplayName("이름에 해당하는 지하철역이 존재하는지 확인한다.")
    void existByName() {
        // given
        stationDao.save(station);

        // when
        boolean actual = stationDao.existByName("선릉역");

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("저장된 지하철 역을 모두 조회한다.")
    void findAll() {
        // given
        stationDao.save(station);

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(1);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 역을 삭제한다.")
    void delete() {
        // given
        Long savedId = stationDao.save(station);

        // when
        stationDao.delete(savedId);

        // then
        boolean result = stationDao.existById(savedId);
        assertFalse(result);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 역이 존재하는지 확인한다.")
    void existById() {
        // given
        Long savedId = stationDao.save(station);

        // when
        boolean result = stationDao.existById(savedId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 역을 조회한다.")
    void findById() {
        // given
        Long savedId = stationDao.save(station);

        // when
        Station actual = stationDao.findById(savedId);

        // then
        Station expected = new Station(savedId, this.station.getName());
        assertThat(actual).isEqualTo(expected);
    }
}
