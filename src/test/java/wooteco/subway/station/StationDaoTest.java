package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StationDaoTest {

    private static final String STATIONNAME1 = "잠실역";
    private static final String STATIONNAME2 = "서울역";

    @Autowired
    private StationDao stationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.update("insert into STATION (name) values (?)", STATIONNAME1);
    }

    @Test
    @DisplayName("역 생성 확인")
    void saveStation() {
        Station savedStation = stationDao.save("가양");
        assertThat(savedStation.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("이름으로 역 검색")
    void findByName() {
        Optional<Station> findStation = stationDao.findByName(STATIONNAME1);
        assertThat(findStation.isPresent()).isTrue();
    }

    @Test
    @DisplayName("id로 역 검색")
    void findById() {
        Station findStation = stationDao.findById(1L).get();
        assertThat("잠실역").isEqualTo(findStation.getName());
    }

    @Test
    @DisplayName("모든 역 검색")
    void findAll() {
        Station savedStation = stationDao.save("가양역");
        Station savedStation2 = stationDao.findByName(STATIONNAME1).get();
        assertThat(stationDao.findAll()).containsExactlyInAnyOrderElementsOf(Arrays.asList(savedStation, savedStation2));
    }

    @Test
    @DisplayName("역 생성 저장 확인")
    void save() {
        stationDao.save(STATIONNAME2);
        assertThat(stationDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("역 삭제 확인")
    void delete() {
        Station savedStation = stationDao.save(STATIONNAME2);
        assertThat(stationDao.findByName(savedStation.getName())).isPresent();

        stationDao.delete(savedStation.getId());
        assertThat(stationDao.findByName(savedStation.getName())).isNotPresent();
    }
}