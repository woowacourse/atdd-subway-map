package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@JdbcTest
class StationRepositoryTest {

    @Autowired
    private DataSource dataSource;

    private StationRepository stationRepository;
    private static final String SILLIM_STATION = "신림역";

    @BeforeEach
    void setUp(){
        stationRepository = new StationRepositoryImpl(dataSource);
    }

    @DisplayName("역을 저장한다.")
    @Test
    void save() {
        Station station = new Station(SILLIM_STATION);
        Station saveStation = stationRepository.save(station);

        assertAll(
                () -> assertThat(saveStation.getId()).isNotNull(),
                () -> assertThat(saveStation).isEqualTo(station)
        );
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station(SILLIM_STATION);
        stationRepository.save(station1);
        Station station2 = new Station("신대방역");
        stationRepository.save(station2);

        List<Station> stations = stationRepository.findAll();

        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(station1, station2)
        );
    }

    @DisplayName("역을 삭제한다")
    @Test
    void deleteById() {
        Station saveStation = stationRepository.save(new Station(SILLIM_STATION));
        stationRepository.deleteById(saveStation.getId());

        assertThat(stationRepository.findAll()).isEmpty();
    }

    @DisplayName("이름으로 역이 존재하는지 조회한다.")
    @Test
    void findByName() {
        stationRepository.save(new Station(SILLIM_STATION));
        Station station = stationRepository.findByName(SILLIM_STATION).get();
        assertThat(station.getName()).isEqualTo(SILLIM_STATION);
    }

    @DisplayName("id로 역을 조회한다.")
    @Test
    void findById() {
        Station saveStation = stationRepository.save(new Station(SILLIM_STATION));
        assertThat(stationRepository.findById(saveStation.getId())).isNotNull();
    }

    @DisplayName("동일한 이름의 역이 존재할 경우 true를 반환한다.")
    @Test
    void existByName() {
        stationRepository.save(new Station(SILLIM_STATION));
        assertThat(stationRepository.existByName(SILLIM_STATION)).isTrue();
    }
}
