package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Sql("/init-station.sql")
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
        jdbcTemplate.update("insert into STATION (name) values (?)", stationName1);
    }

    @Test
    @DisplayName("이름으로 역 검색")
    void findByName() {
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
        assertTrue(stationDao.findByName(savedStation.getName())
                             .isPresent());
        stationDao.delete(savedStation.getId());
        assertFalse(stationDao.findByName(savedStation.getName())
                              .isPresent());
    }
}
