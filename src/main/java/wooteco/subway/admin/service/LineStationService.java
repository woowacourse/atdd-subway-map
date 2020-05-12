package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineStationService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> findAll() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
				.map(line -> LineResponse.of(
						line, stationRepository.findAllById(line.getLineStationsId())))
				.collect(Collectors.toList());
	}

	public Set<LineStation> findLineStation(long lineId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		return line.getStations();
	}

	public LineStationResponse create(LineStationRequest request) {
		Line line = lineRepository.findByName(request.getLineName())
				.orElseThrow(NoSuchElementException::new);
		Station preStation = stationRepository.findByName(request.getPreStationName())
				.orElseThrow(NoSuchElementException::new);
		Station station = stationRepository.findByName(request.getStationName())
				.orElseThrow(NoSuchElementException::new);

		LineStation lineStation = new LineStation(line.getId(), preStation.getId(), station.getId(),
				request.getDistance(), request.getDuration());

		line.addLineStation(lineStation);
		lineRepository.save(line);
		return LineStationResponse.of(lineStation);
	}

	public LineStationResponse removeLineStation(long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		LineStation removedLine = line.removeLineStationById(stationId);
		lineRepository.save(line);
		return LineStationResponse.of(removedLine);
	}
}
