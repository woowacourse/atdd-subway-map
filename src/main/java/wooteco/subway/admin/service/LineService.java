package wooteco.subway.admin.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {

	private LineRepository lineRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return LineResponse.listOf(lines);
	}

	public List<LineResponse> showLinesWithStations() {
		List<Line> lines = lineRepository.findAll();

		Stream<LineStation> allLineStationStream = lines.stream()
			.flatMap(line -> line.getLineStations().stream());
		Map<Long, Station> stations = findStationsBy(allLineStationStream);

		return lines.stream()
			.map(line -> getLineResponses(line, stations))
			.collect(Collectors.toList());
	}

	public Map<Long, Station> findStationsBy(Stream<LineStation> lineStationStream) {
		Set<Long> stationIds = lineStationStream.map(LineStation::getStationId)
			.collect(Collectors.toSet());

		return stationRepository.findAllById(stationIds).stream()
			.collect(Collectors.toMap(Station::getId, station -> station));
	}

	private LineResponse createLineResponseWithStations(Line line) {
		Map<Long, Station> stations = findStationsBy(line.getLineStations().stream());
		return getLineResponses(line, stations);
	}

	private LineResponse getLineResponses(Line line, Map<Long, Station> stations) {
		List<StationResponse> stationResponses = StationResponse
			.listOf(line.getLineStations().stream()
				.map(LineStation::getStationId)
				.map(stations::get)
				.collect(Collectors.toList()));
		return LineResponse.of(line, stationResponses);
	}

	public LineResponse save(Line line) {
		try {
			return LineResponse.of(lineRepository.save(line));
		} catch (DbActionExecutionException dee) {
			if (dee.getCause() instanceof DuplicateKeyException) {
				throw new IllegalArgumentException("라인명은 중복될 수 없습니다.");
			}
			throw dee;
		}
	}

	public LineResponse findLineResponseById(Long id) {
		Line line = findLineById(id);
		return LineResponse.of(line);
	}

	private Line findLineById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
	}

	public LineResponse updateLine(Long id, Line line) {
		Line persistLine = findLineById(id);
		persistLine.update(line);

		Line updatedLine = lineRepository.save(persistLine);
		return LineResponse.of(updatedLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long lineId, LineStationCreateRequest request) {
		Line line = findLineById(lineId);
		LineStation lineStation = makeLineStation(request);
		line.addLineStation(lineStation);

		lineRepository.save(line);
	}

	private LineStation makeLineStation(LineStationCreateRequest request) {
		if (request.hasStationId()) {
			return request.toLineStation();
		}
		return makeLineStationByName(request);
	}

	private LineStation makeLineStationByName(LineStationCreateRequest request) {
		Long preStationId = null;
		if (!request.getPreStationName().isEmpty()) {
			preStationId = findStationIdByName(request.getPreStationName());
		}

		Long stationId = findStationIdByName(request.getStationName());
		return new LineStation(preStationId, stationId, request.getDistance(),
			request.getDuration());
	}

	private Long findStationIdByName(String stationName) {
		return stationRepository.findByName(stationName)
			.map(Station::getId)
			.orElseThrow(
				() -> new NoSuchElementException(String.format("%s역이 등록되어 있지 않습니다.", stationName)));
	}

	public LineResponse findLineResponseWithStationsById(Long id) {
		Line line = findLineById(id);
		return createLineResponseWithStations(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = findLineById(lineId);
		line.removeLineStationById(stationId);

		lineRepository.save(line);
	}
}