package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.LineStationCreateRequest;
import wooteco.subway.admin.dto.resopnse.LineDetailResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class LineStationServiceTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private LineStationService lineStationService;

    @BeforeEach
    void setUp() {
        line = Line.of(1L, "2호선", "bg-green-700", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        lineStationService = new LineStationService(lineRepository, stationRepository);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.existsById(any(Long.class))).thenReturn(true);
        lineStationService.addStationInLine(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(4L);
        assertThat(line.getStationsId().get(1)).isEqualTo(1L);
        assertThat(line.getStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.existsById(any(Long.class))).thenReturn(true);
        lineStationService.addStationInLine(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(4L);
        assertThat(line.getStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.existsById(any(Long.class))).thenReturn(true);
        lineStationService.addStationInLine(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(2L);
        assertThat(line.getStationsId().get(2)).isEqualTo(3L);
        assertThat(line.getStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineStationService.removeStationFromLine(line.getId(), 1L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(2L);
        assertThat(line.getStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineStationService.removeStationFromLine(line.getId(), 2L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineStationService.removeStationFromLine(line.getId(), 3L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Arrays.asList(new Station(1L, "강남역"), new Station(2L, "역삼역"),
            new Station(3L, "삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineDetailResponse lineDetailResponse = lineStationService.findLineWithStationsBy(1L);

        assertThat(lineDetailResponse.getStations()).hasSize(3);
    }
}