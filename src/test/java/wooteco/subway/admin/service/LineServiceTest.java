package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
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
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "#000000");
        lineService = new LineService(lineRepository, stationRepository);

        line.addEdge(new Edge(1L, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest(4L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.getEdgesId().get(0)).isEqualTo(4L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(1L);
        assertThat(line.getEdgesId().get(2)).isEqualTo(2L);
        assertThat(line.getEdgesId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        EdgeCreateRequest request = new EdgeCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(4L);
        assertThat(line.getEdgesId().get(2)).isEqualTo(2L);
        assertThat(line.getEdgesId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(2L);
        assertThat(line.getEdgesId().get(2)).isEqualTo(3L);
        assertThat(line.getEdgesId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 1L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.getEdgesId().get(0)).isEqualTo(2L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 2L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 3L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
        assertThat(line.getEdgesId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        Set<Station> stations = Sets.newLinkedHashSet(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
