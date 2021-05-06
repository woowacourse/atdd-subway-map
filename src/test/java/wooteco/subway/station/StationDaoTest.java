package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class StationDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("station 저장 테스트")
    @Test
    void save() {
        assertThat(stationDao.save(new Station("송파역"))).isEqualTo(1L);
    }

    @DisplayName("station 저장 실패 테스트")
    @Test
    void failSave() {
        stationDao.save(new Station("송파역"));
        assertThatThrownBy(() -> {
            stationDao.save(new Station("송파역"));
        })
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("station 비어있는 리스트 전체 조회 테스트")
    @Test
    void findAllTestWhenEmpty() {
        List<Station> stations = stationDao.findAll();
        assertThat(stations.isEmpty()).isTrue();
    }

    @DisplayName("station 전체 조회 테스트")
    @Test
    void findAllTest() {
        stationDao.save(new Station("송파역"));
        stationDao.save(new Station("잠실역"));
        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.get(0).getId()).isEqualTo(1L);
        assertThat(stations.get(1).getId()).isEqualTo(2L);
    }

    @DisplayName("station 삭제 성공 테스트")
    @Test
    void successDeleteTest() {
        stationDao.save(new Station("송파역"));
        assertDoesNotThrow(() -> stationDao.delete(1L));
    }

    @DisplayName("station 삭제 실패 테스트")
    @Test
    void failDeleteTest() {
        stationDao.save(new Station("송파역"));
        stationDao.delete(1L);

        assertThatThrownBy(() -> {
            stationDao.delete(1L);
        }).isInstanceOf(Exception.class);
    }
}
