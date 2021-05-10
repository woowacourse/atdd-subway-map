package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.SubwayException;

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
        assertThat(stationDao.insert(new Station("송파역"))).isEqualTo(1L);
    }

    @DisplayName("station 저장 실패 테스트")
    @Test
    void failSave() {
        stationDao.insert(new Station("송파역"));
        assertThatThrownBy(
                () -> stationDao.insert(new Station("송파역"))
        ).isInstanceOf(SubwayException.class);
    }

    @DisplayName("station 비어있는 리스트 전체 조회 테스트")
    @Test
    void findAllTestWhenEmpty() {
        List<Station> stations = stationDao.selectAll();
        assertThat(stations).isEmpty();
    }

    @DisplayName("station 전체 조회 테스트")
    @Test
    void findAllTest() {
        stationDao.insert(new Station("송파역"));
        stationDao.insert(new Station("잠실역"));
        List<Station> stations = stationDao.selectAll();
        assertThat(stations).hasSize(2).
                containsExactly(
                        new Station("송파역"),
                        new Station("잠실역")
                );
    }

    @DisplayName("station 삭제 성공 테스트")
    @Test
    void successDeleteTest() {
        stationDao.insert(new Station("송파역"));
        assertDoesNotThrow(() -> stationDao.delete(1L));
    }

    @DisplayName("station 삭제 실패 테스트")
    @Test
    void failDeleteTest() {
        stationDao.insert(new Station("송파역"));
        stationDao.delete(1L);

        assertThatThrownBy(
                () -> stationDao.delete(1L)
        ).isInstanceOf(SubwayException.class);
    }
}
