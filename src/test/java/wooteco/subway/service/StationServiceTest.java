package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("역을 생성한다.")
    @Test
    void createStation() {
        String name = "testStation";
        Station station = new Station(name);
        Station retrivedStation = new Station(1L, name);

        given(stationDao.save(station)).willReturn(1L);
        given(stationDao.findById(1L)).willReturn(retrivedStation);

        Station savedStation = stationService.createStation(name);

        assertThat(savedStation).isEqualTo(retrivedStation);
        verify(stationDao, times(1)).save(station);
        verify(stationDao, times(1)).findById(1L);
    }
}
