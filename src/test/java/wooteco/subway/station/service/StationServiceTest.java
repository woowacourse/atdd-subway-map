package wooteco.subway.station.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.repository.StationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {
    private Station 저장_후_반환된_역 = new Station(1L, "강남역");
    private StationRequest 저장_요청_역 = new StationRequest("강남역");
    private List<Station> 지하철_역들 = Arrays.asList(new Station(1L, "강남역"),
            new Station(2L, "역삼역"), new Station(3L, "잠실역"));

    @Mock
    StationRepository stationRepository = mock(StationRepository.class);

    @InjectMocks
    StationService stationService;

    @DisplayName("지하철 역 저장")
    @Test
    void save() {
        //given
        given(stationRepository.save(any(Station.class))).willReturn(저장_후_반환된_역);

        //when
        StationResponse 응답되는_역 = stationService.save(저장_요청_역);

        //then
        assertThat(응답되는_역.getName()).isEqualTo(저장_요청_역.getName());
        then(stationRepository).should().save(any(Station.class));
    }

    @DisplayName("지하철 역 전체 조회")
    @Test
    void findAll() {
        //given
        given(stationRepository.findAll()).willReturn(지하철_역들);

        //when
        List<StationResponse> 지하철역들 = stationService.findAll();

        //then
        assertThat(지하철_역들).hasSize(지하철역들.size());
        then(stationRepository).should().findAll();
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void delete() {
        //given
        willDoNothing().given(stationRepository).delete(1L);

        //when
        stationService.delete(1L);

        //then
        then(stationRepository).should().delete(1L);
    }

    @DisplayName("순서가 정렬된 지하철 역 조회할 수 있다.")
    @SuppressWarnings("unchecked")
    @Test
    void findSortStationsByIds() {
        //given
        given(stationRepository.findByIds(any(List.class))).willReturn(지하철_역들);

        //when
        Stations 조회한_역들 = stationService.findSortStationsByIds(Arrays.asList(3L, 2L, 1L));

        //then
        then(stationRepository).should().findByIds(any(List.class));
    }
}