package wooteco.subway.admin.line.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.line.domain.line.Line;
import wooteco.subway.admin.line.repository.LineRepository;
import wooteco.subway.admin.line.service.dto.edge.LineStationCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.repository.StationRepository;

@Service
public class LineService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> findAll() {
		return lineRepository.findAll()
		                     .stream()
		                     .map(line -> findLineWithStationsById(line.getId()))
		                     .collect(toList());
	}

	public LineResponse findLineWithStationsById(Long id) {
		final Line line = lineRepository.findById(id)
		                                .orElseThrow(RuntimeException::new);
		final List<Station> stations =
			findSortedStationByLineStationsId(line.getLineStationsId());

		return LineResponse.of(line, stations);
	}

	private List<Station> findSortedStationByLineStationsId(List<Long> lineStationsId) {
		final Map<Long, Station> stations = stationRepository.findAllById(lineStationsId)
		                                                     .stream()
		                                                     .collect(toMap(
			                                                     Station::getId,
			                                                     station -> station));

		return lineStationsId.stream()
		                     .map(stations::get)
		                     .collect(toList());
	}

	public LineResponse save(Line line) {
		return LineResponse.of(lineRepository.save(line));
	}

	public void save(Long id, LineStationCreateRequest request) {
		final Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.addLineStation(request.toLineStation());
		lineRepository.save(persistLine);
	}

	public void update(Long id, Line line) {
		final Line persistLine = lineRepository.findById(id)
		                                       .orElseThrow(RuntimeException::new);
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	public void delete(Long id) {
		lineRepository.deleteById(id);
	}

	public void delete(Long lineId, Long stationId) {
		final Line persistLine = lineRepository.findById(lineId)
		                                       .orElseThrow(RuntimeException::new);
		persistLine.removeLineStationById(stationId);
		lineRepository.save(persistLine);
	}

}
