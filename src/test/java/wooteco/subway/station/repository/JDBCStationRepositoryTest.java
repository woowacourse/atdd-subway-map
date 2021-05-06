package wooteco.subway.station.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.station.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCStationRepositoryTest {

    private Station station;
    private StationRepository stationRepository;

    public JDBCStationRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.stationRepository = new JDBCStationRepository(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        station = new Station("강남역");
        stationRepository.save(station);
    }

    @Test
    void save() {
        //given
        String requestName = "역삼역";
        Station requestStation = new Station(requestName);

        //when
        Station resultStation = stationRepository.save(requestStation);
        String resultName = resultStation.getName();

        //given
        assertThat(requestName).isEqualTo(resultName);
    }

    @Test
    void findAll() {
        //given
        String requestName = "역삼역";
        stationRepository.save(new Station(requestName));

        //when
        List<Station> resultStations = stationRepository.findAll();

        //then
        assertThat(resultStations).hasSize(2);
    }

    @Test
    void findById() {
        //given
        String requestName = "역삼역";
        Station savedStation = stationRepository.save(new Station(requestName));
        Long savedId = savedStation.getId();
        String savedName = savedStation.getName();

        //when
        Station resultStation = stationRepository.findById(savedId);
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
        Station savedStation = stationRepository.save(new Station(requestName));
        Long savedId = savedStation.getId();

        //when
        stationRepository.delete(savedId);
        List<Station> resultStations = stationRepository.findAll();

        //then
        assertThat(resultStations).hasSize(1);
    }
}