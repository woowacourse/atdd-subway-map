package wooteco.subway.admin.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
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
		validateNameDuplicate(line);
		return lineRepository.save(line);
	}

	public List<Line> showLines() {
		return lineRepository.findAll();
	}

	public void updateLine(Long id, Line line) {
		validateNameDuplicate(line);
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	private void validateNameDuplicate(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new IllegalArgumentException("노선 이름이 중복됩니다.");
		}
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStationCreateRequest request) {
		// TODO: 구현
		Line line = lineRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("잘못된 라인 아이디를 입력하였습니다."));
		line.addLineStation(request.toLineStationRequest());
		lineRepository.save(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		// TODO: 구현
		Line line = lineRepository.findById(lineId).orElseThrow(() ->
			new IllegalArgumentException("잘못된 라인 아이디를 입력하였습니다."));
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	public LineResponse findLineWithStationsById(Long id) {
		// TODO: 구현
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		List<Long> lineStationsId = line.findLineStationsId();
		Set<Station> stations = stationRepository.findAllById(lineStationsId);

		return LineResponse.of(line, stations);
	}

	public List<LineStationResponse> findLineStations(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() ->
			new IllegalArgumentException("잘못된 라인 아이디를 입력하였습니다."));
		List<LineStation> lineStations = line.getStations();

		return Collections.unmodifiableList(lineStations.stream()
			.map(LineStationResponse::of)
			.collect(Collectors.toList()));
	}
}
