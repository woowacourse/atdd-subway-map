package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.EdgeRequest;
import wooteco.subway.admin.dto.EdgeResponse;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class EdgeService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public EdgeService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAll() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
				.map(line -> LineResponse.of(
						line, stationRepository.findAllById(line.getEdgesId())))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Set<Edge> findEdge(long lineId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		return line.getStations();
	}

	@Transactional
	public EdgeResponse create(EdgeRequest request) {
		Line line = lineRepository.findById(request.getLineId())
				.orElseThrow(NoSuchElementException::new);

		Edge edge = new Edge(line.getId(), request.getPreStationId(), request.getStationId(),
				request.getDistance(), request.getDuration());

		line.addEdge(edge);
		lineRepository.save(line);
		return EdgeResponse.of(edge);
	}

	@Transactional
	public EdgeResponse remove(long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		Edge removedLine = line.removeEdgeById(stationId);
		lineRepository.save(line);
		return EdgeResponse.of(removedLine);
	}
}
