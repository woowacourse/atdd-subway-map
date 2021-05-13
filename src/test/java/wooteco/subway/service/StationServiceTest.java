package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.StationRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    @DisplayName("역을 생성한다.")
    @Test
    void createStation() {
        String name = "testStation";
        Station station = new Station(name);
        Station retrievedStation = new Station(1L, name);

        given(stationRepository.save(station)).willReturn(1L);
        given(stationRepository.findById(1L)).willReturn(Optional.of(retrievedStation));

        Station savedStation = stationService.createStation(name);

        assertThat(savedStation).isEqualTo(retrievedStation);
        verify(stationRepository, times(1)).save(station);
        verify(stationRepository, times(1)).findById(1L);
    }
}
