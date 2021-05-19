package wooteco.subway.application.station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.presentation.station.dto.StationResponse;
import wooteco.util.StationFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    @Test
    void createStation() {
        Station expected = StationFactory.create("너잘역");
        given(stationRepository.save(any(Station.class)))
                .willReturn(expected);

        Station 너잘역 = stationService.createStation(StationFactory.create("너잘역"));

        assertThat(너잘역.getName()).isEqualTo(expected.getName());
    }

    @Test
    void findAll() {
        List<Station> expected = Arrays.asList(
                StationFactory.create("너잘약"),
                StationFactory.create("데이브역")
        );

        given(stationRepository.findAll()).willReturn(expected);

        List<Station> allStations = stationService.findAll();

        assertThat(allStations)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void findById() {
        given(stationRepository.findById(1L)).willReturn(StationFactory.create("너잘역"));

        Station station = stationService.findById(1L);

        assertThat(station.getName()).isEqualTo("너잘역");
    }
}