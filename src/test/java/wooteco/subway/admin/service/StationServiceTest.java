package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    private StationService stationService;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        stationService = new StationService(stationRepository, lineRepository);

        stations = Arrays.asList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
    }

    @Test
    void save() {
        StationCreateRequest request = new StationCreateRequest("종합운동장역");
        Station expectedStation = request.toStation();
        List<Station> expectedStations = new ArrayList<>(stations);
        expectedStations.add(expectedStation);
        when(stationRepository.findAll()).thenReturn(expectedStations);

        stationService.save(request);

        assertThat(stationService.showStations()).hasSize(4);
        assertThat(stationService.showStations().get(3))
            .isEqualTo(StationResponse.of(expectedStation));
    }

    @Test
    void findAll() {
        when(stationRepository.findAll()).thenReturn(stations);

        assertThat(stationService.showStations()).hasSize(3);
        assertThat(stationService.showStations()).isEqualTo(StationResponse.listOf(stations));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void deleteById(int id) {
        List<Station> expectedStations = new ArrayList<>(stations);
        expectedStations.remove(id - 1);
        when(stationRepository.findAll()).thenReturn(expectedStations);

        stationService.deleteById((long) id);

        assertThat(stationService.showStations()).hasSize(2);
        assertThat(stationService.showStations())
            .isEqualTo(StationResponse.listOf(expectedStations));
    }
}