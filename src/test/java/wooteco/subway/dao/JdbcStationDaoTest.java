package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Station;

@JdbcTest
public class JdbcStationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("지하철역 생성한다.")
    @Test
    void save() {
        Station station = new Station("강남역");
        Station newStation = stationDao.save(station);
        assertThat(newStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("해당 이름을 가진 지하철역이 존재하는지 확인한다.")
    @Test
    void existByName() {
        Station station = new Station("강남역");
        stationDao.save(station);
        assertThat(stationDao.existByName(station.getName())).isTrue();
    }

    @DisplayName("지하철역 목록 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("선릉역");
        stationDao.save(station1);
        stationDao.save(station2);

        assertThat(stationDao.findAll()).hasSize(2);
    }

    @DisplayName("지하철역 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("강남역");
        Station createdStation = stationDao.save(station);
        stationDao.delete(createdStation.getId());

        assertThat(stationDao.findAll()).hasSize(0);
    }

    @DisplayName("해당 id를 가진 지하철역이 존재하는지 확인한다.")
    @Test
    void existById() {
        Station station = new Station("강남역");
        Station createdStation = stationDao.save(station);

        assertThat(stationDao.existById(createdStation.getId())).isTrue();
    }
}
