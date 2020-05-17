package wooteco.subway.admin.line.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.common.exception.SubwayException;
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

	@Transactional
	public LineResponse save(Line line) {
		if (lineRepository.findByName(line.getName()).isPresent()) {
			throw new SubwayException("중복된 이름의 호선이 존재합니다.");
		}

		return LineResponse.of(lineRepository.save(line));
	}

	@Transactional
	public void save(Long id, LineStationCreateRequest request) {
		final Line persistLine = lineRepository.findById(id)
			.orElseThrow(() -> new SubwayException("id에 해당하는 노선이 존재하지 않습니다."));

		persistLine.addLineStation(request.toLineStation());
		lineRepository.save(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAll() {
		return lineRepository.findAll()
			.stream()
			.map(line -> findLineWithStationsById(line.getId()))
			.collect(toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineWithStationsById(Long id) {
		final Line line = lineRepository.findById(id)
			.orElseThrow(() -> new SubwayException("id에 해당하는 노선이 존재하지 않습니다."));
		final List<Station> stations = findSortedStations(line.getLineStationsId());

		return LineResponse.of(line, stations);
	}

	private List<Station> findSortedStations(List<Long> lineStationsId) {
		final Map<Long, Station> stations = getStationsBy(lineStationsId);

		return lineStationsId.stream()
			.map(stations::get)
			.collect(toList());
	}

	private Map<Long, Station> getStationsBy(final List<Long> lineStationsId) {
		return stationRepository.findAllById(lineStationsId)
			.stream()
			.collect(toMap(
				Station::getId,
				station -> station)
			);
	}

	@Transactional
	public void update(Long id, Line line) {
		final Line persistLine = lineRepository.findById(id)
			.orElseThrow(() -> new SubwayException("id에 해당하는 노선이 존재하지 않습니다."));

		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	@Transactional
	public void delete(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void delete(Long lineId, Long stationId) {
		final Line persistLine = lineRepository.findById(lineId)
			.orElseThrow(() -> new SubwayException("id에 해당하는 노선이 존재하지 않습니다."));

		persistLine.removeLineStationById(stationId);
		lineRepository.save(persistLine);
	}

}
