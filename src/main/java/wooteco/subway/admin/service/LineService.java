package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.controller.exception.NoLineExistException;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.controller.request.LineControllerRequest;
import wooteco.subway.admin.dto.controller.request.LineStationControllerRequest;
import wooteco.subway.admin.dto.service.response.LineServiceResponse;
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
	public LineServiceResponse save(LineControllerRequest request) {
		Line persistLine = lineRepository.save(request.toLine());

		return LineServiceResponse.of(persistLine);
	}

	@Transactional
	public void addLineStation(Long lineId, LineStationControllerRequest lineStationControllerRequest) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(NoLineExistException::new);

		LineStation lineStation = lineStationControllerRequest.toLineStation();

		if (lineStation.isFirstLineStation()) {
			persistLine.addLineStationOnFirst(lineStationControllerRequest.toLineStation());
			lineRepository.save(persistLine);
			return;
		}

		persistLine.addLineStation(lineStationControllerRequest.toLineStation());
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
				.orElseThrow(NoLineExistException::new);

		List<Long> stationIds = persistLine.getLineStationsId();
		Iterable<Station> lineStations = stationRepository.findAllById(stationIds);

		return LineWithStationsServiceResponse.of(persistLine, lineStations);
	}

	@Transactional
	public void updateLine(Long id, LineControllerRequest request) {
		Line persistLine = lineRepository.findById(id)
				.orElseThrow(NoLineExistException::new);

		persistLine.update(request.toLine());
		lineRepository.save(persistLine);
	}

	public void deleteLineBy(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void removeLineStation(Long lineId, Long stationId) {
		Line persistLine = lineRepository.findById(lineId)
				.orElseThrow(NoLineExistException::new);

		persistLine.removeLineStationById(stationId);
		lineRepository.save(persistLine);
	}
}
