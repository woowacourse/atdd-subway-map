package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Station;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
    void findByIds() {
        Station station = new Station("강남역");
        Station station2 = new Station("양재역");
        Station station3 = new Station("역삼역");
        stationRepository.save(station);
        stationRepository.save(station2);
        stationRepository.save(station3);

        List<Station> stations = stationRepository.findAll();
        List<Long> targetIds = Arrays.asList(stations.get(0).getId(), stations.get(2).getId());

        stations = stationRepository.findByIds(targetIds);
        assertThat(stations.size()).isEqualTo(2);
    }
}
