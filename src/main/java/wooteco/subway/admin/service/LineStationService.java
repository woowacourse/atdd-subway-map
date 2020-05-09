package wooteco.subway.admin.service;

import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
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

	public LineStation createLineStation(String lineName, String preStationName, String stationName,
			int distance, int duration) {
		Line line = lineRepository.findByName(lineName)
				.orElseThrow(NoSuchElementException::new);
		Station preStation = stationRepository.findByName(preStationName)
				.orElseThrow(NoSuchElementException::new);
		Station station = stationRepository.findByName(stationName)
				.orElseThrow(NoSuchElementException::new);

		LineStation lineStation = new LineStation(line.getId(), preStation.getId(), station.getId(),
				distance, duration);

		line.addLineStation(lineStation);
		lineRepository.save(line);
		return lineStation;
	}

	public Set<LineStation> findLineStation(long lineId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		return line.getStations();
	}

	public LineStation removeLineStation(long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(NoSuchElementException::new);
		return line.removeLineStationById(stationId);
	}
}
