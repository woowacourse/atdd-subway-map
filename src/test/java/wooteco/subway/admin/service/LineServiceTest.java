package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineService lineService;
    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);

        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "blue");

        line.addLineStation(new LineStation(0L, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));

        station1 = new Station("강남역");
        station2 = new Station("역삼역");
        station3 = new Station("삼성역");
        station4 = new Station("구로역");
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(0L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station4));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(station1));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(station2));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(station3));

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station1));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(station4));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(station2));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(station3));

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station1));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(station2));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(station3));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(station4));

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getLineStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(3L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getLineStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station1));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(station2));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(station3));
        when(stationRepository.findAllById(anyList())).thenReturn(Arrays.asList(station1, station2, station3));

        LineResponse lineResponse = lineService.showLine(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
