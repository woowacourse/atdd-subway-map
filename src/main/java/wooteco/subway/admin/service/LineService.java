package wooteco.subway.admin.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.exception.NoLineWithSuchIdException;

@Service
public class LineService {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public List<LineResponse> showLines() {
		List<Line> persistLines = lineRepository.findAll();

		Map<Line, List<Station>> lineWithStations = persistLines.stream()
			.collect(Collectors.toMap(
				Function.identity(),
				persistLine -> toStations(persistLine.getAllStationIds())
			));

		return LineResponse.listOf(lineWithStations);
	}

	public LineResponse showLine(Long id) {
		Line persistLine = lineRepository.findById(id).orElseThrow(NoLineWithSuchIdException::new);

		List<Station> stations = toStations(persistLine.getAllStationIds());

		return LineResponse.of(persistLine, stations);
	}

	public LineResponse updateLine(Long id, LineRequest lineRequest) {
		Line persistLine = lineRepository.findById(id).orElseThrow(NoLineWithSuchIdException::new);
		persistLine.update(lineRequest.toLine());
		Line updatedLine = lineRepository.save(persistLine);

		List<Station> stations = toStations(updatedLine.getAllStationIds());

		return LineResponse.of(updatedLine, stations);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse addLineStation(Long lineId, LineStationCreateRequest lineStationCreateRequest) {
		Line line = lineRepository.findById(lineId).orElseThrow(NoLineWithSuchIdException::new);
		LineStation lineStation = lineStationCreateRequest.toLineStation();
		line.addLineStation(lineStation);
		Line persistLine = lineRepository.save(line);
		List<Station> stations = toStations(persistLine.getAllStationIds());
		return LineResponse.of(persistLine, stations);
	}

	public void removeLineStation(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(NoLineWithSuchIdException::new);
		line.removeLineStationById(stationId);
		lineRepository.save(line);
	}

	public List<Station> toStations(List<Long> lineStationsId) {
		return stationRepository.findAllById(lineStationsId);
	}

	public LineResponse createLine(LineRequest lineRequest) {
		Line persistLine = lineRepository.save(lineRequest.toLine());
		List<Station> stations = toStations(persistLine.getAllStationIds());

		return LineResponse.of(persistLine, stations);
	}
}
