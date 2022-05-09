package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.FakeStationDao;
import wooteco.subway.repository.exception.DuplicateStationNameException;

@JdbcTest
class StationRepositoryTest {

    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        this.stationRepository = new StationRepository(new FakeStationDao());
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        Station station = new Station("강남역");
        Long stationId = stationRepository.save(station);
        assertThat(stationId).isGreaterThan(0L);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 저장한다.")
    @Test
    void saveWithExistentName() {
        String name = "강남역";
        stationRepository.save(new Station(name));
        assertThatThrownBy(() -> stationRepository.save(new Station(name)))
                .isInstanceOf(DuplicateStationNameException.class)
                .hasMessageContaining("해당 이름의 지하철역은 이미 존재합니다.");
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void findAll() {
        List<Station> stations = List.of(
                new Station("강남역"),
                new Station("역삼역"),
                new Station("선릉역")
        );
        stations.forEach(stationRepository::save);
        assertThat(stationRepository.findAll()).hasSize(3);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void remove() {
        Long stationId = stationRepository.save(new Station("강남역"));
        stationRepository.remove(stationId);
        assertThat(stationRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 지하철역을 삭제한다.")
    @Test
    void removeWithNonexistentId() {
        assertThatThrownBy(() -> stationRepository.remove(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("삭제하고자 하는 지하철역이 존재하지 않습니다.");
    }
}