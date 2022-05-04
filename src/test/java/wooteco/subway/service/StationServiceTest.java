package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

class StationServiceTest {

    private final StationDao stationDao = new FakeStationDao();
    private final StationService stationService = new StationService(stationDao);

    @BeforeEach
    void setUp() {
        List<Station> stations = stationDao.findAll();
        List<Long> stationIds = stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        Station station = new Station("범고래");

        // when
        Station result = stationService.save(station);

        // then
        assertThat(station).isEqualTo(result);
    }

    @Test
    void validateDuplication() {
        // given
        Station station1 = new Station("범고래");
        Station station2 = new Station("범고래");

        // when
        stationService.save(station1);

        // then
        assertThatThrownBy(() -> stationService.save(station2))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAll() {
        // given
        Station station1 = stationService.save(new Station("범고래"));
        Station station2 = stationService.save(new Station("애쉬"));

        // when
        List<Station> stations = stationService.findAll();

        // then
        assertThat(stations)
            .hasSize(2)
            .contains(station1, station2);
    }

    @Test
    void deleteById() {
        // given
        Station station = stationService.save(new Station("범고래"));

        // when
        stationService.deleteById(station.getId());
        List<Station> stations = stationService.findAll();

        // then
        assertThat(stations)
            .hasSize(0)
            .doesNotContain(station);
    }
}
