package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.EdgeRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationRepository stationRepository;

	private Line line;
	private EdgeService edgeService;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "bg-red-500", "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
		edgeService = new EdgeService(lineRepository, stationRepository);

		line.addEdge(new Edge(1L, null, 1L, 10, 10));
		line.addEdge(new Edge(1L, 1L, 2L, 10, 10));
		line.addEdge(new Edge(1L, 2L, 3L, 10, 10));
	}

	@DisplayName("처음 위치에 엣지 추가")
	@Test
	void addLineStationAtTheFirstOfLine() {
		EdgeRequest request = new EdgeRequest(1L, null, 4L);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.create(request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getEdgesId().get(0)).isEqualTo(4L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(1L);
		assertThat(line.getEdgesId().get(2)).isEqualTo(2L);
		assertThat(line.getEdgesId().get(3)).isEqualTo(3L);
	}

	@DisplayName("두 엣지 사이에 엣지 추가")
	@Test
	void addLineStationBetweenTwo() {
		EdgeRequest request = new EdgeRequest(1L, 1L, 4L);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.create(request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(4L);
		assertThat(line.getEdgesId().get(2)).isEqualTo(2L);
		assertThat(line.getEdgesId().get(3)).isEqualTo(3L);
	}

	@DisplayName("마지막 위치에 엣지 추가")
	@Test
	void addLineStationAtTheEndOfLine() {
		EdgeRequest request = new EdgeRequest(1L, 3L, 4L);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.create(request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(2L);
		assertThat(line.getEdgesId().get(2)).isEqualTo(3L);
		assertThat(line.getEdgesId().get(3)).isEqualTo(4L);
	}

	@DisplayName("처음 위치에 엣지 제거")
	@Test
	void removeLineStationAtTheFirstOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.remove(line.getId(), 1L);

		assertThat(line.getEdgesId()).hasSize(2);
		assertThat(line.getEdgesId().get(0)).isEqualTo(2L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(3L);
	}

	@DisplayName("두 엣지 사이에 있는 엣지 제거")
	@Test
	void removeLineStationBetweenTwo() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.remove(line.getId(), 2L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(3L);
	}

	@DisplayName("마지막에 있는 엣지 제거")
	@Test
	void removeLineStationAtTheEndOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		edgeService.remove(line.getId(), 3L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getEdgesId().get(0)).isEqualTo(1L);
		assertThat(line.getEdgesId().get(1)).isEqualTo(2L);
	}
}
