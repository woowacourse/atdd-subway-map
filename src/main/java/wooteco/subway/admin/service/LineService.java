package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
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

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public LineResponse save(Line line) {
		boolean sameName = showLines().stream()
			.anyMatch(element -> element.getName().equals(line.getName()));

		if (sameName) {
			throw new IllegalArgumentException("중복되는 역 이름입니다.");
		}

		return LineResponse.of(lineRepository.save(line));
	}

	public List<Line> showLines() {
		return lineRepository.findAll();
	}

	public LineResponse updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);
		Line updatedLine = lineRepository.save(persistLine);
		return LineResponse.of(updatedLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long lineId, LineStationCreateRequest request) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
		checkPreStation(request, line);

		LineStation lineStation = request.toLineStation();
		line.addLineStation(lineStation);

		lineRepository.save(line);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
		line.removeLineStationById(stationId);

		lineRepository.save(line);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));

		List<Long> stationIds = line.getStations().stream()
			.map(LineStation::getStationId)
			.collect(Collectors.toList());

		List<Station> stations = stationRepository.findAllById(stationIds);
		return LineResponse.of(line, stations);
	}

	private void checkPreStation(LineStationCreateRequest request, Line line) {
		Long preStationId = request.getPreStationId();
		if (preStationId != null && hasNotPreStation(line, preStationId)) {
			throw new IllegalArgumentException("이전역을 찾을 수 없습니다.");
		}
	}

	private boolean hasNotPreStation(Line line, Long preStationId) {
		return line.getStations().stream()
			.noneMatch(lineStation -> lineStation.getStationId().equals(preStationId));
	}
}