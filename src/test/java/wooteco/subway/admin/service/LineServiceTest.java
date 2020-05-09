package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-orange-500");
        lineService = new LineService(lineRepository, stationRepository);

        line.addLineStation(new LineStation(null, 1L));
        line.addLineStation(new LineStation(1L, 2L));
        line.addLineStation(new LineStation(2L, 3L));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStationRequest request = new LineStationRequest(line.getId(), null, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        LineStationRequest request = new LineStationRequest(line.getId(), 1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationRequest request = new LineStationRequest(line.getId(), 3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(3L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Lists.newArrayList(new Station("강남역"), new Station("역삼역"),
            new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
