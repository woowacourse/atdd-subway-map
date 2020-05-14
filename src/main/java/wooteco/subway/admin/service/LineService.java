package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.exceptions.AlreadySavedException;
import wooteco.subway.admin.service.exceptions.NotFoundException;

@Service
public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public Long save(Line line) {
		lineRepository.findByName(line.getName())
			.ifPresent(this::throwAlreadySavedException);

		Line persistLine = lineRepository.save(line);
		return persistLine.getId();
	}

	private void throwAlreadySavedException(Line line) {
		throw new AlreadySavedException("이미 존재하는 노선 이름입니다. name: " + line.getName());
	}

	@Transactional(readOnly = true)
	public List<LineResponse> getLineResponses() {
		List<Line> lines = lineRepository.findAll();

		// TODO 로직 개선
		return lines.stream()
			.map(line -> LineResponse.of(line, stationRepository.findAllById(line.getEdgesId())))
			.collect(Collectors.toList());
	}

	@Transactional
	public void updateLine(Long id, Line line) {
		Line persistLine = findLineById(id);
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	@Transactional
	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addEdge(Long id, EdgeCreateRequest request) {
		Line line = findLineById(id);
		Edge edge = request.toEdge();
		line.addEdge(edge);
		lineRepository.save(line);
	}

	private Line findLineById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> throwNotFoundException(id));
	}

	private NotFoundException throwNotFoundException(Long id) {
		return new NotFoundException("데이터를 찾을 수 없습니다. id: " + id);
	}

	@Transactional
	public void removeEdge(Long lineId, Long stationId) {
		Line line = findLineById(lineId);
		line.removeEdgeById(stationId);
		lineRepository.save(line);
	}

	@Transactional(readOnly = true)
	public LineResponse findLineWithStationsById(Long id) {
		Line line = findLineById(id);
		return LineResponse.of(line, stationRepository.findAllById(line.getEdgesId()));
	}
}
