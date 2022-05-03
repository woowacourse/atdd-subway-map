package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

}
