package wooteco.subway.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class StationRepositoryTest {

    private static final Long UP_STATION_ID = 1L;
    private static final Long MIDDLE_STATION_ID = 2L;
    private static final Long DOWN_STATION_ID = 3L;

    @Autowired
    private DataSource dataSource;

    private StationRepository stationRepository;
    private static final String SILLIM_STATION = "신림역";

    @BeforeEach
    void setUp() {
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
        List<Station> stations = stationRepository.findAll();
        List<Long> ids = stations.stream().map(Station::getId).collect(Collectors.toList());
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(ids).containsExactly(UP_STATION_ID, MIDDLE_STATION_ID, DOWN_STATION_ID)
        );

    }

    @DisplayName("역을 삭제한다")
    @Test
    void deleteById() {
        Station station = stationRepository.findById(MIDDLE_STATION_ID)
                .orElseThrow(() -> new NotFoundException("[ERROR] 식별자에 해당하는 역을 찾을 수 없습니다."));
        stationRepository.delete(station);

        assertThat(stationRepository.findAll()).hasSize(2);
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
