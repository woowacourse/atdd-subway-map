package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public LineResponse save(Line line) {
		return LineResponse.of(lineRepository.save(line));
	}

	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return LineResponse.listOf(lines);
	}

	public void updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);

		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long lineId, LineStationCreateRequest request) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
		LineStation lineStation = request.toLineStation();
		line.addLineStation(lineStation);

		lineRepository.save(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
		line.removeLineStationById(stationId);

		lineRepository.save(line);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));

		return createLineResponse(line);
	}

	public List<LineResponse> showLinesWithStations() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(this::createLineResponse)
			.collect(Collectors.toList());
	}

	public LineStationCreateRequest findLineByName(LineStationCreateByNameRequest request) {
		System.out.println(request.getPreStationName());
		System.out.println(request.getStationName());

		Long preStationId = stationRepository.findIdByName(request.getPreStationName());
		Long stationId = stationRepository.findIdByName(request.getStationName());
		System.out.println(preStationId);
		System.out.println(stationId);
		return new LineStationCreateRequest(preStationId, stationId, request.getDistance(), request.getDuration());
	}

	private LineResponse createLineResponse(Line line) {
		List<Station> stations = stationRepository.findAllByIdOrderBy(line.getId());
		// List<Long> lineStationsIds = line.getLineStationsId();
		// List<Station> stations = stationRepository.findAllById(lineStationsIds);

		return LineResponse.of(line, stations);
	}
}