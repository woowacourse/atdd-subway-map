package wooteco.subway.admin.service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	Map<Long, Line> lines = new HashMap<>();
	Map<Long, Station> stations = new HashMap<>();
	Map<Long, Set<LineStation>> lineStations = new HashMap<>();

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
		lines.put(1L, new Line(1L, "2호선", "green", LocalTime.MIN, LocalTime.MAX, 3));
		stations.put(1L, new Station(1L, "잠실역"));
		stations.put(2L, new Station(2L, "종합운동장역"));
		stations.put(3L, new Station(3L, "선릉역"));
		stations.put(4L, new Station(4L, "강남역"));
		lineStations.put(1L, new HashSet<>());
	}

	public LineResponse save(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new RuntimeException();
		}
		return LineResponse.of(lineRepository.save(line));
	}

	public List<LineResponse> showLines() {
		return LineResponse.listOf(lineRepository.findAll());
	}

	public LineResponse updateLine(Long id, Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new RuntimeException();
		}
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);
		return LineResponse.of(lineRepository.save(persistLine));
	}

	public LineResponse showLine(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		return LineResponse.of(line);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStationCreateRequest request) {
		// TODO: 구현
		lineStations.get(id).add(request.toLineStation());
	}

	public void removeLineStation(Long lineId, Long stationId) {
		// TODO: 구현
		lineStations.get(lineId).remove(
			lineStations.get(lineId).stream()
				.filter(lineStation -> lineStation.getStationId() == stationId)
				.findFirst()
				.get());
	}

	public LineResponse findLineWithStationsById(Long id) {
		// TODO: 구현
		Line line = lines.get(id);
		Set<Station> mapper = lineStations.get(id)
			.stream()
			.map(lineStation -> stations.get(lineStation.getStationId()))
			.collect(Collectors.toSet());
		return new LineResponse(id, line.getName(), line.getColor(), line.getStartTime(), line.getEndTime(),
			line.getIntervalTime(), line.getCreatedAt(), line.getUpdatedAt(), mapper);
	}
}
