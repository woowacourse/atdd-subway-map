package wooteco.subway.station.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import wooteco.subway.station.api.dto.StationRequest;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

@MockitoSettings
class StationServiceTest {

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private StationService stationService;

    @DisplayName("역을 생성하는 기능")
    @Test
    void createStation() {
        //given
        when(stationDao.save(any())).thenReturn(1L);
        when(stationDao.findStationById(1L)).thenReturn(new Station(1L, "잠실역"));
        StationRequest request = new StationRequest("잠실역");

        //when
        StationResponse stationResponse = stationService.createStation(request);

        //then
        assertThat(stationResponse.getId()).isEqualTo(1L);
        assertThat(stationResponse.getName()).isEqualTo("잠실역");
    }

    @DisplayName("모든 역을 찾는 기능")
    @Test
    void findAll() {
        //given
        List<Station> stations = Arrays.asList(
            new Station(1L, "잠실역"),
            new Station(2L, "잠실새내역")
        );
        when(stationDao.findAll()).thenReturn(stations);

        //when
        List<StationResponse> stationResponses = stationService.findAll();

        //then
        assertAll(
            () -> assertThat(stationResponses).hasSize(2),
            () -> assertThat(stationResponses.get(0).getId()).isEqualTo(1L),
            () -> assertThat(stationResponses.get(0).getName()).isEqualTo("잠실역"),
            () -> assertThat(stationResponses.get(1).getId()).isEqualTo(2L),
            () -> assertThat(stationResponses.get(1).getName()).isEqualTo("잠실새내역")
        );
    }
}
