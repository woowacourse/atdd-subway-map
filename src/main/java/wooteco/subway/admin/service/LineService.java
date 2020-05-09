package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

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

	public Line updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id)
				.orElseThrow(RuntimeException::new);

		persistLine.update(line);
		return lineRepository.save(persistLine);
	}

	public void deleteLineBy(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long lineId, LineStationCreateRequest request) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		if (request.getPreStationId() == null) {
			persistLine.addLineStationOnFirst(request.toLineStation());
			return;
		}

		persistLine.addLineStation(request.toLineStation());
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		persistLine.removeLineStationById(stationId);
	}

	public LineResponse findLineWithStationsBy(Long lineId) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		return LineResponse.of(persistLine);
	}
}
