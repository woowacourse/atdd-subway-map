package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public Line save(Line line) {
		return lineRepository.save(line);
	}

	public List<Line> showLines() {
		return lineRepository.findAll();
	}

	public void updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStationCreateRequest request) {
		Line line = lineRepository.findById(id)
				.orElseThrow(NoSuchElementException::new);
		LineStation lineStation = new LineStation(id, request.getPreStationId(),
				request.getStationId(), request.getDistance(), request.getDuration());
		line.addLineStation(lineStation);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		line.removeLineStationById(stationId);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
				.orElseThrow(NoSuchElementException::new);
		Set<Station> stations = stationRepository.findAllById(line.getLineStationsId());
		return LineResponse.of(line, stations);
	}
}
