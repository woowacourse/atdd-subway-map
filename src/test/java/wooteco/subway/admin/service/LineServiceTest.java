package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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
        line = new Line(1L,"비내리는호남선",  LocalTime.of(05, 30), LocalTime.of(22, 30),5, "bg-yellow-700");
        lineService = new LineService(lineRepository, stationRepository);

        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));
    }

    @DisplayName("노선 맨 앞에 역을 추가했을때")
    @Test
    void addEdgeAtTheFirstOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest("", "까치산역", 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.findByName("까치산역")).thenReturn(Optional.of(new Station(4L, "까치산역")));
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdgeIds()).hasSize(4);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(4L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(1L);
        assertThat(line.getEdgeIds().get(2)).isEqualTo(2L);
        assertThat(line.getEdgeIds().get(3)).isEqualTo(3L);
    }

    @DisplayName("노선 가운데에 역을 추가했을때")
    @Test
    void addEdgeBetweenTwo() {
        EdgeCreateRequest request = new EdgeCreateRequest("강남역", "까치산역", 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.findByName("강남역")).thenReturn(Optional.of(new Station(1L, "강남역")));
        when(stationRepository.findByName("까치산역")).thenReturn(Optional.of(new Station(4L, "까치산역")));

        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdgeIds()).hasSize(4);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(1L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(4L);
        assertThat(line.getEdgeIds().get(2)).isEqualTo(2L);
        assertThat(line.getEdgeIds().get(3)).isEqualTo(3L);
    }

    @DisplayName("노선 맨 끝에 역을 추가했을때")
    @Test
    void addEdgeAtTheEndOfLine() {
        EdgeCreateRequest request = new EdgeCreateRequest("삼성역", "까치산역", 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.findByName("삼성역")).thenReturn(Optional.of(new Station(3L, "삼성역")));
        when(stationRepository.findByName("까치산역")).thenReturn(Optional.of(new Station(4L, "까치산역")));

        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdgeIds()).hasSize(4);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(1L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(2L);
        assertThat(line.getEdgeIds().get(2)).isEqualTo(3L);
        assertThat(line.getEdgeIds().get(3)).isEqualTo(4L);
    }

    @DisplayName("노선 맨 앞의 역을 제거했을때")
    @Test
    void removeEdgeAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.deleteStationByLineIdAndStationId(line.getId(), 1L);

        assertThat(line.getEdgeIds()).hasSize(2);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(2L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(3L);
    }

    @DisplayName("노선 가운데 역을 제거했을때")
    @Test
    void removeEdgeBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.deleteStationByLineIdAndStationId(line.getId(), 2L);

        assertThat(line.getEdgeIds()).hasSize(2);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(1L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(3L);
    }

    @DisplayName("노선 맨 끝의 역을 제거했을때")
    @Test
    void removeEdgeAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.deleteStationByLineIdAndStationId(line.getId(), 3L);

        assertThat(line.getEdgeIds()).hasSize(2);
        assertThat(line.getEdgeIds().get(0)).isEqualTo(1L);
        assertThat(line.getEdgeIds().get(1)).isEqualTo(2L);
    }

    @DisplayName("노선id로 그 노선에 포함된 모든 정보(역 목록 등) 가져오기 테스트")
    @Test
    void findLineWithStationsById() {
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findStationsByLineId(1L);

        assertThat(lineResponse.getTitle()).isEqualTo("비내리는호남선");
    }
}
