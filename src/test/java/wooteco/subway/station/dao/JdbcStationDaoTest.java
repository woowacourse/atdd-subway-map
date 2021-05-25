package wooteco.subway.station.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.infra.JdbcStationDao;
import wooteco.subway.station.infra.StationDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JdbcStationDaoTest {

    private Station station;
    private StationDao stationDao;

    public JdbcStationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        station = new Station("강남역");
        stationDao.save(station);
    }

    @Test
    void save() {
        //given
        String requestName = "역삼역";
        Station requestStation = new Station(requestName);

        //when
        Station resultStation = stationDao.save(requestStation);
        String resultName = resultStation.getName();

        //given
        assertThat(requestName).isEqualTo(resultName);
    }

    @Test
    void findAll() {
        //given
        String requestName = "역삼역";
        stationDao.save(new Station(requestName));

        //when
        List<Station> resultStations = stationDao.findAll();

        //then
        assertThat(resultStations).hasSize(2);
    }

    @Test
    void findById() {
        //given
        String requestName = "역삼역";
        Station savedStation = stationDao.save(new Station(requestName));
        Long savedId = savedStation.getId();
        String savedName = savedStation.getName();

        //when
        Station resultStation = stationDao.findById(savedId).get();
        Long resultId = resultStation.getId();
        String resultName = resultStation.getName();

        //then
        assertThat(resultId).isEqualTo(savedId);
        assertThat(resultName).isEqualTo(savedName);
    }

    @Test
    void delete() {
        //given
        String requestName = "역삼역";
        Station savedStation = stationDao.save(new Station(requestName));
        Long savedId = savedStation.getId();

        //when
        stationDao.delete(savedId);
        List<Station> resultStations = stationDao.findAll();

        //then
        assertThat(resultStations).hasSize(1);
    }
}