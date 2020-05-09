package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.LineStationRepository;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class LineService {
	private LineRepository lineRepository;
	private LineStationRepository lineStationRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository,
	                   LineStationRepository lineStationRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.lineStationRepository = lineStationRepository;
		this.stationRepository = stationRepository;
	}

	public Line save(Line line) {
		return lineRepository.save(line);
	}

	public List<Line> findAll() {
		return lineRepository.findAll();
	}

	public void updateLine(Long id, Line line) {
		Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		persistLine.update(line);
		lineRepository.save(persistLine);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void addLineStation(Long id, LineStationCreateRequest request) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존지하지 않습니다"));

		LineStation toInput = new LineStation(request.getPreStationId(), request.getStationId(), request.getDistance(),
				request.getDuration());
		line.updatePreStationWhenAdd(toInput);
		line.addLineStation(toInput);
		lineRepository.save(line);
	}

	public List<LineStation> findLineStationByLineId(Long lineId) {
		return lineStationRepository.findAllByLineId(lineId);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다"));
		line.updatePreStationWhenRemove(stationId);
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	public LineResponse findLineWithStationsById(Long id) {
		Line line = lineRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다"));

		LineResponse lineResponse = LineResponse.of(line);
		List<Station> stations = line.getLineStationsId().stream()
				.map(stationId -> stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new))
				.collect(Collectors.toList());
		lineResponse.setStations(stations);
		System.out.println(">>> "+ lineResponse.getStations());
		return lineResponse;
	}

	public void validateTitle(LineRequest lineRequest) {
		lineRepository.findByTitle(lineRequest.getTitle()).ifPresent(line -> {
			throw new IllegalArgumentException("존재하는 이름입니다");
		});
	}

	public void validateTitleWhenUpdate(Long id, LineRequest lineRequest) throws Exception {
		Line lineById = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		if (lineById.getTitle().equals(lineRequest.getTitle())) {
			return;
		}
		validateTitle(lineRequest);
	}

	public Line findById(Long lineId) {
		return lineRepository.findById(lineId).orElseThrow(IllegalAccessError::new);
	}

	public Line findByTitle(String lineName) {
		return lineRepository.findByTitle(lineName).orElseThrow(IllegalArgumentException::new);
	}
}
