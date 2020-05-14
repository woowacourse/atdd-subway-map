package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.controller.request.LineCreateControllerRequest;
import wooteco.subway.admin.dto.controller.request.LineStationCreateControllerRequest;
import wooteco.subway.admin.dto.service.response.LineCreateServiceResponse;
import wooteco.subway.admin.dto.service.response.LineWithStationsServiceResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional
	public LineCreateServiceResponse save(LineCreateControllerRequest request) {
		Line persistLine = lineRepository.save(request.toLine());

		return LineCreateServiceResponse.of(persistLine);
	}

	@Transactional
	public void addLineStation(Long lineId, LineStationCreateControllerRequest lineStationCreateControllerRequest) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		LineStation lineStation = lineStationCreateControllerRequest.toLineStation();

		if (lineStation.isFirstLineStation()) {
			persistLine.addLineStationOnFirst(lineStationCreateControllerRequest.toLineStation());
			lineRepository.save(persistLine);
			return;
		}

		persistLine.addLineStation(lineStationCreateControllerRequest.toLineStation());
		lineRepository.save(persistLine);
	}

	public List<LineWithStationsServiceResponse> findLines() {
		return lineRepository.findAll()
				.stream()
				.map(line -> findLineWithStationsBy(line.getId()))
				.collect(Collectors.toList());
	}

	public LineWithStationsServiceResponse findLineWithStationsBy(Long lineId) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		List<Long> stationIds = persistLine.getLineStationsId();
		List<Station> stations = stationRepository.findAllById(stationIds);

		return LineWithStationsServiceResponse.of(persistLine, stations);
	}

	@Transactional
	public void updateLine(Long id, LineCreateControllerRequest request) {
		Line persistLine = lineRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		persistLine.update(request.toLine());
		lineRepository.save(persistLine);
	}

	public void deleteLineBy(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void removeLineStation(Long lineId, Long stationId) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(() -> new IllegalArgumentException("해당 id의 line이 없습니다."));

		persistLine.removeLineStationById(stationId);
		lineRepository.save(persistLine);
	}
}
