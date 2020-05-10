package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

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
		final Line persistLine = lineRepository.findById(id)
		                                       .orElseThrow(RuntimeException::new);
		List<Station> stations = stationRepository.findAllById(persistLine.getLineStationsId());

		return LineResponse.of(persistLine, stations);
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
