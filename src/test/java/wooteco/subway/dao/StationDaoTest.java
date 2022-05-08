package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.getStation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Station station = new Station("강남역");

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장하고 아이디로 찾는다.")
    @Test
    void saveAndFind() {
        Long id = stationDao.save(station);
        assertThat(stationDao.findById(id))
                .isEqualTo(getStation(id, station));
    }

    @DisplayName("해당 이름의 지하철역이 있는지 확인한다.")
    @Test
    void hasStation_name() {
        stationDao.save(station);
        assertThat(stationDao.hasStation(station.getName()))
                .isTrue();
        assertThat(stationDao.hasStation("선릉역"))
                .isFalse();
    }

    @DisplayName("해당 id의 지하철역이 있는지 확인한다.")
    @Test
    void hasStation_id() {
        Long id = stationDao.save(station);

        assertThat(stationDao.hasStation(id))
                .isTrue();
        assertThat(stationDao.hasStation(100L))
                .isFalse();
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findAll() {
        //given
        Station station1 = new Station("선릉역");
        Long id = stationDao.save(station);
        Long id2 = stationDao.save(station1);

        //when then
        assertThat(stationDao.findAll())
                .containsOnly(getStation(id, station), getStation(id2, station1));
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        //given
        Long id = stationDao.save(station);

        //when
        stationDao.delete(id);

        //then
        assertThat(stationDao.hasStation(id))
                .isFalse();
    }
}
