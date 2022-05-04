package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private StationService stationService;
    private StationDao fakeStationDao;

    @BeforeEach
    void setUp() {
        fakeStationDao = new FakeStationDao();
        stationService = new StationService(fakeStationDao);
    }

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        StationResponse response = stationService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        // given
        fakeStationDao.save(new Station("노원역"));
        fakeStationDao.save(new Station("왕십리역"));

        // when
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역을 삭제한다.")
    void delete() {
        // given
        Station savedStation = fakeStationDao.save(new Station("마들역"));

        // when
        stationService.delete(savedStation.getId());

        // then
        List<Station> remainStations = fakeStationDao.findAll();
        assertThat(remainStations).hasSize(0);
    }
}
