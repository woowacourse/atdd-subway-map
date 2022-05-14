package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import wooteco.subway.domain.station.StationSeries;

@SpringBootTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("persist를 통해 저장한다.")
    public void saveByPersist() {
        // given
        StationSeries stationSeries = new StationSeries(List.of());
        // when
        stationSeries.add(STATION_A);
        stationRepository.persist(stationSeries);

        // then
        assertThat(stationRepository.findAllStations()).hasSize(1);
    }

    @Test
    @DisplayName("persist를 통해 삭제한다.")
    public void deleteByPersist() {
        // given
        StationSeries stationSeries = new StationSeries(List.of(STATION_A, STATION_B));

        // when
        stationSeries.add(STATION_C);
        stationRepository.persist(stationSeries);
        // then
        assertThat(stationRepository.findAllStations()).hasSize(3);
    }
}