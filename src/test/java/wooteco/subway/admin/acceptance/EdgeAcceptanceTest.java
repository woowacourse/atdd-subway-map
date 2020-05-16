package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.EdgeRequest;
import wooteco.subway.admin.dto.EdgeResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.EdgeService;

@ExtendWith(MockitoExtension.class)
public class EdgeAcceptanceTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationRepository stationRepository;

	private EdgeService edgeService;
	private Line line;
	private Station preStation;
	private Station station;

	@BeforeEach
	void setUp() {
		edgeService = new EdgeService(lineRepository, stationRepository);
		line = new Line(1L, "1호선", "bg-red-500", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
		preStation = new Station(2L, "잠실");
		station = new Station(3L, "석촌");
	}

	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageEdge() {
		EdgeRequest request = new EdgeRequest("8호선", "잠실", "석촌");

		when(lineRepository.findByName("8호선")).thenReturn(Optional.of(line));
		when(stationRepository.findByName("잠실")).thenReturn(Optional.of(preStation));
		when(stationRepository.findByName("석촌")).thenReturn(Optional.of(station));

		EdgeResponse created = edgeService.create(request);
		assertThat(created.getLineId()).isEqualTo(line.getId());
		assertThat(created.getPreStationId()).isEqualTo(preStation.getId());
		assertThat(created.getStationId()).isEqualTo(station.getId());

		when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

		Set<Edge> edges = edgeService.findEdge(line.getId());
		Set<EdgeResponse> edgeResponses = edges.stream()
				.map(EdgeResponse::of)
				.collect(Collectors.toSet());
		assertThat(edgeResponses.contains(created)).isTrue();

		when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

		EdgeResponse removed = edgeService.remove(line.getId(), station.getId());
		assertThat(created).isEqualTo(removed);

		Set<Edge> edgesAfterRemove = edgeService.findEdge(line.getId());
		Set<EdgeResponse> edgeResponsesAfterRemove = edgesAfterRemove.stream()
				.map(EdgeResponse::of)
				.collect(Collectors.toSet());
		assertThat(edgeResponsesAfterRemove.contains(removed)).isFalse();
	}
}
