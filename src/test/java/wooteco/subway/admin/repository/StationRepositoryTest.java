package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Test
    void save() {
        Station station = new Station("강남역");

        Station persistStation = stationRepository.save(station);

        assertThat(persistStation.getId()).isNotNull();
    }

    @Test
    void findByName() {
        Station station = new Station("회룡역");
        stationRepository.save(station);

        assertThat(stationRepository.findByName("회룡역").isPresent()).isTrue();
    }
}
