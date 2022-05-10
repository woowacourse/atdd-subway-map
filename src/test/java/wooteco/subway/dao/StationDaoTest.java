package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station 추가")
    void save() {
        Station station = stationDao.save(new Station("선릉역"));

        assertThat(station.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("Station 목록 조회")
    void findAll() {
        Station station1 = stationDao.save(new Station("선릉역"));
        Station station2 = stationDao.save(new Station("잠실역"));

        assertAll(
                () -> assertThat(stationDao.findAll()).hasSize(2),
                () -> assertThat(stationDao.findAll()).contains(station1),
                () -> assertThat(stationDao.findAll()).contains(station2)
        );
    }

    @Test
    @DisplayName("특정 Station 삭제")
    void deleteById() {
        Station station = stationDao.save(new Station("선릉역"));
        stationDao.deleteById(station.getId());
        List<Station> stations = stationDao.findAll();

        assertThat(stations).doesNotContain(station);
    }
}