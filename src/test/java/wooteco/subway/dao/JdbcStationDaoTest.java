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
class JdbcStationDaoTest {

    private JdbcStationDao jdbcStationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcStationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void save() {
        long actual = jdbcStationDao.save(new Station("강남역"));
        assertThat(actual).isNotNull();
    }

    @DisplayName("전체 지하철역들을 조회한다.")
    @Test
    void findAll() {
        jdbcStationDao.save(new Station("강남역"));
        jdbcStationDao.save(new Station("양제역"));

        List<Station> actual = jdbcStationDao.findAll();

        assertThat(actual).containsExactly(new Station("강남역"), new Station("양제역"));
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteById() {
        Long id = jdbcStationDao.save(new Station("강남역"));
        boolean isDeleted = jdbcStationDao.deleteById(id);
        assertThat(isDeleted).isTrue();
    }

    @DisplayName("지하철역을 Id로 조회한다.")
    @Test
    void findById() {
        Long id = jdbcStationDao.save(new Station("강남역"));
        Station station = jdbcStationDao.findById(id);
        assertThat(station.getName()).isEqualTo("강남역");
    }
}
