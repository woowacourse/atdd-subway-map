package wooteco.subway.admin.service;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.line.domain.edge.Edges;
import wooteco.subway.admin.line.domain.repository.LineRepository;
import wooteco.subway.admin.line.service.LineService;
import wooteco.subway.admin.line.service.dto.edge.EdgeCreateRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeDeleteRequest;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.domain.repository.StationRepository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-green-200");
        lineService = new LineService(lineRepository, stationRepository);

        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));
    }

    @DisplayName("출발역을 추가한다.")
    @Test
    void addLineStationAtTheFirstOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest(null, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(4);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(4L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(2).getStationId()).isEqualTo(2L);
        assertThat(edges.getEdges().get(3).getStationId()).isEqualTo(3L);
    }

    @DisplayName("중간에 역 끼워넣기")
    @Test
    void addLineStationBetweenTwo() {
        EdgeCreateRequest request = new EdgeCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(4);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(4L);
        assertThat(edges.getEdges().get(2).getStationId()).isEqualTo(2L);
        assertThat(edges.getEdges().get(3).getStationId()).isEqualTo(3L);
    }

    @DisplayName("마지막에 역 추가하기")
    @Test
    void addLineStationAtTheEndOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(4);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(2L);
        assertThat(edges.getEdges().get(2).getStationId()).isEqualTo(3L);
        assertThat(edges.getEdges().get(3).getStationId()).isEqualTo(4L);
    }

    @DisplayName("출발역 제거하기")
    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), new EdgeDeleteRequest(1L));

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(2);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(2L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(3L);
    }

    @DisplayName("중간역 제거하기")
    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), new EdgeDeleteRequest(2L));

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(2);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(3L);
    }

    @DisplayName("마지막 역 제거하기")
    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), new EdgeDeleteRequest(3L));

        Edges edges = line.getEdges();

        assertThat(edges.getEdges()).hasSize(2);
        assertThat(edges.getEdges().get(0).getStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(1).getStationId()).isEqualTo(2L);
    }

    @DisplayName("해당 노선에 존재하는 구간 정보 전체 조회하기")
    @Test
    void findLineWithStationsById() {
        Set<Station> stations = Sets.newLinkedHashSet(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
