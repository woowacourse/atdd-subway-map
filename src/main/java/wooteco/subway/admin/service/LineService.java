package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.exception.DuplicateLineNameException;
import wooteco.subway.admin.exception.NotFoundLineException;
import wooteco.subway.admin.exception.NotFoundStationException;
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

	@Transactional(readOnly = true)
	public LineResponse findLine(Long lineId) {
		return LineResponse.of(findLineById(lineId), stationRepository.findStations(lineId));
	}

	@Transactional(readOnly = true)
	public List<LineStation> findLineStations(Long lineId) {
		Line line = findLineById(lineId);
		return line.getStations();
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();
		List<Station> allStations = stationRepository.findAll();
		return lines.stream()
			.map(line -> LineResponse.of(line, line.findContainingStationsFrom(allStations)))
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	public LineResponse save(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new DuplicateLineNameException();
		}
		return LineResponse.of(lineRepository.save(line));
	}

	public void updateLine(Long id, Line updatedLine) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		if (persistLine.isNotSameName(updatedLine) && lineRepository.existsByName(updatedLine.getName())) {
			throw new DuplicateLineNameException();
		}
		persistLine.update(updatedLine);
		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		if (!lineRepository.existsById(id)) {
			throw new NotFoundLineException();
		}
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long lineId, LineStation lineStation) {
		Line line = findLineById(lineId);
		validateStationId(lineStation.getPreStationId());
		validateStationId(lineStation.getStationId());
		line.addLineStation(lineStation);
		lineRepository.save(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = findLineById(lineId);
		validateStationId(stationId);
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	private void validateStationId(Long stationId) {
		if (stationId != null && !stationRepository.existsById(stationId)) {
			throw new NotFoundStationException();
		}
	}

	private Line findLineById(Long lineId) {
		return lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
	}
}

