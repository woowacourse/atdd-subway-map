package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.service.dto.station.StationFindResponse;
import wooteco.subway.service.dto.station.StationSaveRequest;
import wooteco.subway.service.dto.station.StationSaveResponse;

class StationEntityServiceTest {

    private final StationDao stationDao = new FakeStationDao();
    private final StationService stationService = new StationService(stationDao);

    @BeforeEach
    void setUp() {
        List<StationEntity> stationEntities = stationDao.findAll();
        List<Long> stationIds = stationEntities.stream()
            .map(StationEntity::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        StationSaveRequest station = new StationSaveRequest("범고래");

        // when
        StationSaveResponse result = stationService.save(station);

        // then
        String stationName = station.getName();
        String resultName = result.getName();
        assertThat(stationName).isEqualTo(resultName);
    }

    @Test
    void validateDuplication() {
        // given
        StationEntity stationEntity1 = new StationEntity("범고래");
        StationEntity stationEntity2 = new StationEntity("범고래");

        // when
        stationService.save(new StationSaveRequest(stationEntity1.getName()));

        // then
        assertThatThrownBy(() -> stationService.save(new StationSaveRequest(stationEntity2.getName())))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAll() {
        // given
        StationSaveResponse station1 = stationService.save(new StationSaveRequest("범고래"));
        StationSaveResponse station2 = stationService.save(new StationSaveRequest("애쉬"));

        // when
        List<StationFindResponse> stations = stationService.findAll();

        // then
        assertThat(stations).filteredOn((station) -> station.getName().equals(station1.getName()))
            .hasSize(1);
        assertThat(stations).filteredOn((station) -> station.getName().equals(station2.getName()))
            .hasSize(1);
    }

    @Test
    void deleteById() {
        // given
        StationSaveResponse savedStation = stationService.save(new StationSaveRequest("범고래"));

        // when
        stationService.deleteById(savedStation.getId());
        List<StationFindResponse> stations = stationService.findAll();

        // then
        assertThat(stations).filteredOn(
                (station) -> station.getName().equals(savedStation.getName()))
            .hasSize(0);
    }
}
