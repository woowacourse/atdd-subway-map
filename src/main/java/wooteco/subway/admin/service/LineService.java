package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
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

	public void addLineStation(Long id, LineStationCreateRequest request) {
		// TODO: 구현
	}

	public void removeLineStation(Long lineId, Long stationId) {
		// TODO: 구현
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("라인이 없습니다."));
		return LineResponse.of(line);
	}
}
