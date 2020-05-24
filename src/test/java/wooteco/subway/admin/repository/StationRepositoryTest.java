package wooteco.subway.admin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Sql("/truncate.sql")
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
        stationRepository.save(new Station("강남역"));
        stationRepository.save(new Station("잠실역"));
        stationRepository.save(new Station("신림역"));
    }

    @DisplayName("모든 역을 찾는지 테스트")
    @Test
    void findAllTest() {
        List<Station> stations = stationRepository.findAll();
        assertThat(stations.size()).isEqualTo(3);
    }

    @DisplayName("id들의 리스트로 해당하는 역을 찾을 수 있는지 테스트")
    @Test
    void findAllByIdTest() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Station> stations = stationRepository.findAllById(ids);
        assertThat(stations.get(0).getId()).isEqualTo(1L);
        assertThat(stations.get(1).getId()).isEqualTo(2L);
    }

    @DisplayName("이름으로 역을 찾을 수 있는지 테스트")
    @Test
    void findByNameTest() {
        Station station1 = stationRepository.findByName("강남역").orElseThrow(NoSuchElementException::new);
        Station station2 = stationRepository.findByName("잠실역").orElseThrow(NoSuchElementException::new);
        Station station3 = stationRepository.findByName("신림역").orElseThrow(NoSuchElementException::new);

        assertThat(station1.getId()).isEqualTo(1L);
        assertThat(station2.getId()).isEqualTo(2L);
        assertThat(station3.getId()).isEqualTo(3L);
    }
}
