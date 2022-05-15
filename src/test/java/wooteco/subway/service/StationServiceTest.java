package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.StationService.DUPLICATE_EXCEPTION_MESSAGE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.mock.MemoryStationDao;

class StationServiceTest {
    private MemoryStationDao stationDao = new MemoryStationDao();
    private StationService stationService = new StationService(stationDao);

    @BeforeEach
    void beforeEach() {
        stationDao.clear();
    }

    @Test
    void saveDuplicate() {
        String name = "강남역";
        stationService.create(new Station(name));

        assertThatThrownBy(() -> stationService.create(new Station(name)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

}
