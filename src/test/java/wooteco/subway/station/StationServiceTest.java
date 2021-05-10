package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        final String name = "잠실역";
        final Station station = new Station(name);
        final StationRequest stationRequest = spy(new StationRequest(name));
        given(stationDao.save(station)).willReturn(new Station(1L, name));

        final StationResponse createdStation = stationService.createStation(stationRequest);

        verify(stationRequest, times(1)).toEntity();
        verify(stationDao, times(1)).save(station);
        assertThat(createdStation.getId()).isEqualTo(1L);
    }
}
