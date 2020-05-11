package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Transactional
@Service
public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public Line save(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new IllegalArgumentException("노선 이름이 중복됩니다.");
		}
		return lineRepository.save(line);
	}

	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(line -> LineResponse.of(line, stationRepository.findAllById(line.findLineStationsId())))
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	public void updateLine(Long id, Line updatedLine) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		if (persistLine.isNotSameName(updatedLine) && lineRepository.existsByName(updatedLine.getName())) {
			throw new IllegalArgumentException("노선 이름이 중복됩니다.");
		}
		persistLine.update(updatedLine);
		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		if (!lineRepository.existsById(id)) {
			throw new IllegalArgumentException("존재하지 않는 리소스를 이용할 수 없습니다.");
		}
		lineRepository.deleteById(id);
	}

	public LineStationResponse addLineStation(Long lineId, LineStationCreateRequest request) {
		Line line = findLineById(lineId);
		LineStation lineStation = request.toLineStation();
		validateStationId(lineStation.getPreStationId());
		validateStationId(lineStation.getStationId());
		line.addLineStation(lineStation);
		lineRepository.save(line);
		return LineStationResponse.of(lineId, lineStation);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = findLineById(lineId);
		validateStationId(stationId);
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	private void validateStationId(Long stationId) {
		if (stationId != null && !stationRepository.existsById(stationId)) {
			throw new IllegalArgumentException("존재하지 않는 역입니다.");
		}
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = findLineById(id);
		List<Long> lineStationsId = line.findLineStationsId();
		Set<Station> stations = stationRepository.findAllById(lineStationsId);

		return LineResponse.of(line, stations);
	}

	public List<LineStationResponse> findLineStations(Long id) {
		Line line = findLineById(id);
		List<LineStation> lineStations = line.getStations();

		return Collections.unmodifiableList(lineStations.stream()
			.map(lineStation -> LineStationResponse.of(id, lineStation))
			.collect(toList()));
	}

	private Line findLineById(Long lineId) {
		return lineRepository.findById(lineId).orElseThrow(() ->
			new IllegalArgumentException("존재하지 않는 노선입니다."));
	}
}

