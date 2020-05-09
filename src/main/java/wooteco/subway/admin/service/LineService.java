package wooteco.subway.admin.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicatedLineException;
import wooteco.subway.admin.exception.NotFoundLineException;
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
		checkExistLine(line);
		return lineRepository.save(line);
	}

	public List<Line> showLines() {
		return lineRepository.findAll();
	}

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineException::new);
    }

	public void updateLine(Long id, Line line) {
		Line persistLine = findLineById(id);
		if (!Objects.equals(persistLine.getName(), line.getName())) {
			checkExistLine(line);
		}
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	private void checkExistLine(Line line) {
		if (lineRepository.existsByName(line.getName())) {
			throw new DuplicatedLineException(line.getName());
		}
	}

	public void addLineStation(Long id, LineStationCreateRequest request) {
		// TODO: 구현
	}

	public void removeLineStation(Long lineId, Long stationId) {
		// TODO: 구현
	}

	public LineResponse findLineWithStationsById(Long id) {
		// TODO: 구현
		return new LineResponse();
	}
}
