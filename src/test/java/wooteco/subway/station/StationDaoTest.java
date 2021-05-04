package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StationDaoTest {

    private static final String stationName1 = "잠실역";
    private static final String stationName2 = "서울역";
    @Autowired
    private StationDao stationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("delete from STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.update("insert into STATION (name) values (?)", stationName1);
    }

    @Test
    @DisplayName("이름으로 역 검색")
    void findById() {
        Optional<Station> findStation = stationDao.findByName(stationName1);
        assertTrue(findStation.isPresent());
    }

    @Test
    @DisplayName("모든 역 검색")
    void findAll() {
        assertThat(stationDao.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("역 생성 저장 확인")
    void save() {
        stationDao.save(stationName2);
        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("역 삭제 확인")
    void delete() {
        Station savedStation = stationDao.save(stationName2);
        assertThat(stationDao.findByName(stationName2)).isNotNull();
        stationDao.delete(savedStation.getId());
        Optional<Station> findStation = stationDao.findByName(savedStation.getName());
        assertFalse(findStation.isPresent());
    }
}