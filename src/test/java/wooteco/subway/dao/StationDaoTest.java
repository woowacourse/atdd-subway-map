package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

@JdbcTest
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;
    private StationResponse station;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        station = stationDao.save(new Station("선릉역"));
    }

    @Test
    @DisplayName("Station 추가")
    void save() {
        assertThat(station.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("Station 목록 조회")
    void findAll() {
        var station2 = stationDao.save(new Station("잠실역"));

        assertAll(
                () -> assertThat(stationDao.findAll()).hasSize(2),
                () -> assertThat(stationDao.findAll()).contains(station),
                () -> assertThat(stationDao.findAll()).contains(station2)
        );
    }

    @Test
    @DisplayName("특정 Station 삭제")
    void deleteById() {
        stationDao.deleteById(station.getId());
        var stations = stationDao.findAll();

        assertThat(stations).doesNotContain(station);
    }

    @Test
    void findByUpStationsIdAndDownStationId() {
        var station2 = stationDao.save(new Station("잠실역"));

        assertThat(stationDao.findByUpStationsIdAndDownStationId(station.getId(), station2.getId())).hasSize(2);
    }
}