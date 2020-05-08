package wooteco.subway.admin.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
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

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStationCreateRequest lineStationCreateRequest) {
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		line.addLineStation(lineStationCreateRequest.toLineStation());
		lineRepository.save(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		Set<Station> stations = stationRepository.findAllById(line.getLineStationsId());
		return LineResponse.of(line, stations);
	}

	public LineResponse findById(final Long id) {
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		return LineResponse.of(line);
	}
}
