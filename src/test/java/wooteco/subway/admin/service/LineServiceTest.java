package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.EdgeCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private Map<String, Long> stations = new LinkedHashMap<>();

    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", "bg-green-500", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        lineService = new LineService(lineRepository, stationRepository);

        Station station1 = new Station(1L, "잠실역");
        Station station2 = new Station(2L, "잠실나루역");
        Station station3 = new Station(3L, "강변역");
        Station station4 = new Station(4L, "구의역");
        stations.put(station1.getName(), station1.getId());
        stations.put(station2.getName(), station2.getId());
        stations.put(station3.getName(), station3.getId());
        stations.put(station4.getName(), station4.getId());

        lenient().when(lineRepository.save(line)).thenReturn(line);
        lenient().when(stationRepository.findIdByName(null)).thenReturn(null);
        lenient().when(stationRepository.findIdByName("잠실역")).thenReturn(1L);
        lenient().when(stationRepository.findIdByName("잠실나루역")).thenReturn(2L);
        lenient().when(stationRepository.findIdByName("강변역")).thenReturn(3L);
        lenient().when(stationRepository.findIdByName("구의역")).thenReturn(4L);
        lenient().when(stationRepository.findById(1L)).thenReturn(Optional.of(station1));
        lenient().when(stationRepository.findById(2L)).thenReturn(Optional.of(station2));
        lenient().when(stationRepository.findById(3L)).thenReturn(Optional.of(station3));
        lenient().when(stationRepository.findById(4L)).thenReturn(Optional.of(station4));
        lenient().when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        line.addEdge(Edge.ofFirst(1L, 10, 10));
        line.addEdge(Edge.of(1L, 2L, 10, 10));
        line.addEdge(Edge.of(2L, 3L, 10, 10));
    }

    @Test
    void addEdgeAtTheFirstOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest(null, stations.get("구의역"), 10, 10);

        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.findStationsId().get(0)).isEqualTo(4L);
        assertThat(line.findStationsId().get(1)).isEqualTo(1L);
        assertThat(line.findStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addEdgeBetweenTwo() {
        EdgeCreateRequest request = new EdgeCreateRequest(stations.get("잠실역"), stations.get("구의역"), 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request);

        List<Station> staitonss = lineService.findStationsAtLine(line);

        for (Station station : staitonss) {
            System.out.println(station.getName());
        }
        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.findStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findStationsId().get(1)).isEqualTo(4L);
        assertThat(line.findStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addEdgeAtTheEndOfLine() {
        EdgeCreateRequest request2 = new EdgeCreateRequest(stations.get("강변역"), stations.get("구의역"), 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addEdge(line.getId(), request2);

        assertThat(line.getEdges()).hasSize(4);
        assertThat(line.findStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findStationsId().get(1)).isEqualTo(2L);
        assertThat(line.findStationsId().get(2)).isEqualTo(3L);
        assertThat(line.findStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeEdgeAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 1L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.findStationsId().get(0)).isEqualTo(2L);
        assertThat(line.findStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeEdgeBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 2L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.findStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeEdgeAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 3L);

        assertThat(line.getEdges()).hasSize(2);
        assertThat(line.findStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        lenient().when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        lenient().when(stationRepository.findAllById(anyList())).thenReturn(stations);

        List<Station> stationsAtLine = lineService.findStationsAtLine(line);

        assertThat(stationsAtLine).hasSize(3);
    }
}