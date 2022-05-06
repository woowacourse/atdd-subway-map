package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장하고 아이디로 찾는다.")
    @Test
    void saveAndFind() {
        //given
        Station station = new Station("강남역");

        //when
        Long id = stationDao.save(station);
        assertThat(stationDao.findById(id))
                .isEqualTo(station);
    }

    @DisplayName("해당 이름의 지하철역이 있는지 확인한다.")
    @Test
    void hasStation_name() {
        Station station = new Station("강남역");
        stationDao.save(station);

        assertThat(stationDao.hasStation("강남역"))
                .isTrue();
        assertThat(stationDao.hasStation("선릉역"))
                .isFalse();
    }

    @DisplayName("해당 id의 지하철역이 있는지 확인한다.")
    @Test
    void hasStation_id() {
        Station station = new Station("강남역");
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
        Station station = new Station("강남역");
        Station station1 = new Station("선릉역");
        stationDao.save(station);
        stationDao.save(station1);

        //when
        assertThat(stationDao.findAll())
                .containsOnly(station, station1);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        //given
        Station station = new Station("강남역");

        //when
        stationDao.delete(stationDao.findById(stationDao.save(station)).getId());

        //then
        assertThat(stationDao.findAll()).hasSize(0);
    }
}
