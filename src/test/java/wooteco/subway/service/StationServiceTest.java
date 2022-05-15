package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationServiceRequest;
import wooteco.subway.service.dto.StationServiceResponse;

@JdbcTest
class StationServiceTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new StationDaoImpl(jdbcTemplate);
        stationService = new StationService(stationDao);
        List<Station> stationEntities = stationDao.findAll();
        List<Long> stationIds = stationEntities.stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        StationServiceRequest station = new StationServiceRequest("범고래");

        // when
        StationServiceResponse result = stationService.save(station);

        // then
        String stationName = station.getName();
        String resultName = result.getName();
        assertThat(stationName).isEqualTo(resultName);
    }

    @Test
    void validateDuplication() {
        // given
        Station station1 = new Station("범고래");
        Station station2 = new Station("범고래");

        // when
        stationService.save(new StationServiceRequest(station1.getName()));

        // then
        assertThatThrownBy(
            () -> stationService.save(new StationServiceRequest(station2.getName())))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAll() {
        // given
        StationServiceResponse station1 = stationService.save(new StationServiceRequest("범고래"));
        StationServiceResponse station2 = stationService.save(new StationServiceRequest("애쉬"));

        // when
        List<StationServiceResponse> stations = stationService.findAll();

        // then
        assertThat(stations).filteredOn((station) -> station.getName().equals(station1.getName()))
            .hasSize(1);
        assertThat(stations).filteredOn((station) -> station.getName().equals(station2.getName()))
            .hasSize(1);
    }

    @Test
    void deleteById() {
        // given
        StationServiceResponse savedStation = stationService.save(new StationServiceRequest("범고래"));

        // when
        stationService.deleteById(savedStation.getId());
        List<StationServiceResponse> stations = stationService.findAll();

        // then
        assertThat(stations).filteredOn(
                (station) -> station.getName().equals(savedStation.getName()))
            .hasSize(0);
    }
}
