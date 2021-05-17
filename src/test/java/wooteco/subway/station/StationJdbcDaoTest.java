package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StationJdbcDaoTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE STATION");
        jdbcTemplate.execute("ALTER TABLE STATION ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO STATION (name) VALUES (?)", "강남역");
    }

    private final Station station = new Station("잠실역");


    @DisplayName("역 저장 테스트")
    @Test
    void save() {
        Station savedStation = stationDao.save(station);
        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("역 목록 조회 테스트")
    @Test
    void findAll() {
        List<Station> savedStation = Arrays.asList(station, new Station("강남역"));
        stationDao.save(station);

        assertThat(stationDao.findAll()).containsExactlyInAnyOrderElementsOf(savedStation);
    }

    @DisplayName("역 삭제 테스트")
    @Test
    void delete() {
        Station savedStation = stationDao.save(station);
        stationDao.delete(savedStation.getId());

        List<Station> remainStation = Arrays.asList(new Station("강남역"));

        assertThat(stationDao.findAll()).containsExactlyInAnyOrderElementsOf(remainStation);
    }

    @DisplayName("유효하지 않은 역 아이디 조회 시 true 반환")
    @Test
    void doesNotExist() {
        assertThat(stationDao.doesNotExist(10L)).isTrue();
    }
}