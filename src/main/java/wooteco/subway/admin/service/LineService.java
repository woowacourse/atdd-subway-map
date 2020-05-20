package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateByNameRequest;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
	private static final String LINE_DATA_NO_SUCH_MESSAGE = "라인이 없습니다.";

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public LineResponse save(Line line) {
		return LineResponse.of(lineRepository.save(line));
	}

	@Transactional(readOnly = true)
	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return LineResponse.listOf(lines);
	}

	@Transactional
	public void updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);

		lineRepository.save(persistLine);
	}

	@Transactional
	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addLineStation(Long lineId, LineStationCreateRequest request) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException(LINE_DATA_NO_SUCH_MESSAGE));
		LineStation lineStation = request.toLineStation();
		line.addLineStation(lineStation);

		lineRepository.save(line);
	}

	@Transactional
	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException(LINE_DATA_NO_SUCH_MESSAGE));
		line.removeLineStationById(stationId);

		lineRepository.save(line);
	}

	@Transactional(readOnly = true)
	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException(LINE_DATA_NO_SUCH_MESSAGE));

		return createLineResponse(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> showLinesWithStations() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(this::createLineResponse)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineStationCreateRequest findLineByName(LineStationCreateByNameRequest request) {
		Long preStationId = stationRepository.findIdByName(request.getPreStationName());
		Long stationId = stationRepository.findIdByName(request.getStationName());
		return new LineStationCreateRequest(preStationId, stationId, request.getDistance(), request.getDuration());
	}

	private LineResponse createLineResponse(Line line) {
		List<Station> stations = stationRepository.findAllByIdOrderBy(line.getId());
		return LineResponse.of(line, stations);
	}
}