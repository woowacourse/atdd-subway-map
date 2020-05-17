package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.DuplicatedValueException;
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

	public Line save(Line line) {
		if (isDuplicatedTitle(line.getTitle())) {
			throw new DuplicatedValueException(line.getTitle());
		}

		try {
			return lineRepository.save(line);
		} catch (DbActionExecutionException e) {
			throw new DuplicatedValueException(line.getTitle());
		}
	}

	public Line findLine(Long id) {
		return lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
	}

	public List<LineResponse> showLines() {
		return lineRepository.findAll()
			.stream()
			.map(x -> findLineWithStationsById(x.getId()))
			.collect(Collectors.toList());
	}

	public void updateLine(Long id, Line line) {
		if (isDuplicatedTitle(line.getTitle())) {
			throw new DuplicatedValueException(line.getTitle());
		}
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);

		try {
			lineRepository.save(persistLine);
		} catch (DuplicateKeyException e) {
			if (e.getCause() instanceof DuplicateKeyException) {
				throw new DuplicatedValueException(persistLine.getTitle());
			}
			throw e;
		}
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStation request) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		if (request.getPreStationId() == null) {
			Station station = stationRepository.findById(request.getStationId()).orElseThrow(RuntimeException::new);
			persistLine.addLineStation(
				new LineStation(null, station.getId(), request.getDistance(), request.getDuration()));
			lineRepository.save(persistLine);
			return;
		}
		Station preStation = stationRepository.findById(request.getPreStationId()).orElseThrow(RuntimeException::new);
		Station station = stationRepository.findById(request.getStationId()).orElseThrow(RuntimeException::new);
		persistLine.addLineStation(
			new LineStation(preStation.getId(), station.getId(), request.getDistance(), request.getDuration()));
		lineRepository.save(persistLine);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line persistLine = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
		persistLine.removeLineStationById(stationId);
		lineRepository.save(persistLine);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		List<Station> stations = persistLine.getLineStationsId()
			.stream()
			.map(stationId -> stationRepository.findById(stationId))
			.map(station -> station.orElseThrow(NoSuchElementException::new))
			.collect(Collectors.toList());

		return LineResponse.of(persistLine, StationResponse.of(stations));
	}

	private boolean isDuplicatedTitle(String title) {
		return lineRepository.findByName(title)
			.isPresent();
	}
}
