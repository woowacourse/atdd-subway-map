package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
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
	public Line findLine(Long id) {
		return findLineById(id);
	}

	@Transactional(readOnly = true)
	public List<LineStation> findLineStations(Long id) {
		Line line = findLineById(id);
		return line.getStations();
	}

	@Transactional(readOnly = true)
	public List<Line> findAllLines() {
		return lineRepository.findAll();
	}

	public Line save(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new DuplicateLineNameException();
		}
		return lineRepository.save(line);
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

