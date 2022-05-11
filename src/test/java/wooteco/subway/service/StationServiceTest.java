package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.service.fakeDao.StationDaoImpl;

public class StationServiceTest {

    private final StationService stationService = new StationService(StationDaoImpl.getInstance());

    @BeforeEach
    void setUp() {
        final StationDaoImpl stationDao = StationDaoImpl.getInstance();
        final List<Station> stations = stationDao.findAll();
        stations.clear();
    }

    @Test
    @DisplayName("이미 존재하는 역을 생성하려고 하면 에러를 발생한다.")
    void save_duplicate_station() {
        final StationRequest stationRequest1 = new StationRequest("강남역");
        final StationRequest stationRequest2 = new StationRequest("강남역");

        stationService.saveStation(stationRequest1);

        assertThatThrownBy(() -> stationService.saveStation(stationRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 역이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 역을 접근하려고 하면 에러를 발생한다.")
    void not_exist_station() {
        final StationRequest stationRequest = new StationRequest("강남역");

        final StationResponse stationResponse = stationService.saveStation(stationRequest);
        final Long invalidStationId = stationResponse.getId() + 1L;

        assertThatThrownBy(() -> stationService.deleteStation(invalidStationId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당하는 역이 존재하지 않습니다.");
    }
}
