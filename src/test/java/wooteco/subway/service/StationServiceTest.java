package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static wooteco.subway.Fixtures.HYEHWA;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.CreateStationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.notfound.NotFoundStationException;
import wooteco.subway.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    @Test
    @DisplayName("역을 등록한다.")
    void createStation() {
        // given
        final Long id = 1L;
        final String name = "한성대입구역";
        final Station savedStation = new Station(id, name);
        final CreateStationRequest request = new CreateStationRequest(name);

        // mocking
        given(stationRepository.save(any())).willReturn(id);
        given(stationRepository.findById(id)).willReturn(savedStation);

        // when
        final StationResponse response = stationService.createStation(request);

        // then
        assertThat(response.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void showStations() {
        // given
        final Station savedStation1 = new Station("한성대입구역");
        final Station savedStation2 = new Station("신대방역");

        // mocking
        given(stationRepository.findAll()).willReturn(Arrays.asList(savedStation1, savedStation2));

        // when
        final List<StationResponse> responses = stationService.showStations();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("역을 조회한다.")
    void show() {
        // mocking
        given(stationRepository.findById(any(Long.class))).willReturn(new Station(1L, HYEHWA));

        // when
        final Station station = stationService.show(1L);

        // then
        assertThat(station.getName()).isEqualTo(HYEHWA);
    }

    @Test
    @DisplayName("없는 역을 조회하면, 예외를 발생시킨다.")
    void showNotExistStation() {
        // mocking
        given(stationRepository.findById(any(Long.class))).willThrow(NotFoundStationException.class);

        // when & then
        assertThatThrownBy(() -> stationService.show(1L))
                .isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void deleteStation() {
        // given
        final long id = 1L;

        // mocking
        given(stationRepository.existsById(id)).willReturn(true);

        // when
        stationService.deleteStation(id);

        // then
        verify(stationRepository).deleteById(id);
    }

    @Test
    @DisplayName("없는 역을 삭제하면, 예외를 발생시킨다.")
    void deleteWithNotExistStation() {
        // mocking
        given(stationRepository.existsById(any(Long.class))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> stationService.deleteStation(any(Long.class)))
                .isInstanceOf(NotFoundStationException.class);
    }
}
