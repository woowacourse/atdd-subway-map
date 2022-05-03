package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class StationRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @DisplayName("역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("신림역");
        Station saveStation = stationRepository.save(station);

        assertAll(
                () -> assertThat(saveStation.getId()).isEqualTo(1L),
                () -> assertThat(saveStation).isEqualTo(station)
        );
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        Station station1 = new Station("신림역");
        stationRepository.save(station1);
        Station station2 = new Station("신대방역");
        stationRepository.save(station2);

        List<Station> stations = stationRepository.findAll();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).containsExactly(station1, station2);
    }
}
