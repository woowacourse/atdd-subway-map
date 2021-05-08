package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StationH2DaoTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM STATION");
        jdbcTemplate.execute("ALTER TABLE STATION ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO STATION (name) VALUES (?)", "강남역");
    }

    @DisplayName("역 저장 테스트")
    @Test
    void save() {
        Station station = new Station("잠실역");
        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("역 목록 조회 테스트")
    @Test
    void findAll() {
        Station station = new Station("잠실역");
        stationDao.save(station);

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @DisplayName("역 삭제 테스트")
    @Test
    void delete() {
        Station station = new Station("잠실역");
        Station savedStation = stationDao.save(station);
        stationDao.delete(savedStation.getId());

        assertThat(stationDao.findAll()).hasSize(1);
    }
}