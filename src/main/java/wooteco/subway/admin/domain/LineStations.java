package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.controller.exception.LineStationCreateException;
import wooteco.subway.admin.controller.exception.NoLineExistException;
import wooteco.subway.admin.controller.exception.NoStationExistException;

import java.util.List;
import java.util.stream.Collectors;

public class LineStations {
	private static final int ADD_ON_FIRST_INDEX = 0;
	private static final int FIRST_INDEX = 0;
	private static final int ONE_STATION = 1;

	@MappedCollection(idColumn = "line", keyColumn = "line_key")
	private List<LineStation> lineStations;

	private LineStations(List<LineStation> lineStations) {
		this.lineStations = lineStations;
	}

	public static LineStations of(List<LineStation> stations) {
		return new LineStations(stations);
	}

	public void addLineStationOnFirst(LineStation inputLineStation) {
		validateFirstLineStationFormat(inputLineStation);

		if (lineStations.isEmpty()) {
			lineStations.add(ADD_ON_FIRST_INDEX, inputLineStation);
			return;
		}

		LineStation lineStation = lineStations.stream()
				.filter(LineStation::isFirstLineStation)
				.findFirst()
				.orElseThrow(() -> new NoStationExistException("처음 역이 없습니다."));

		lineStation.updatePreStationId(inputLineStation.getStationId());
		lineStations.add(ADD_ON_FIRST_INDEX, inputLineStation);
	}


	private void validateFirstLineStationFormat(LineStation inputLineStation) {
		if (inputLineStation.isNotFirstLineStation()) {
			throw new LineStationCreateException("처음에 추가할 수 없는 구간 형태입니다.");
		}
	}

	public void addLineStation(LineStation inputLineStation) {
		LineStation preLineStation = lineStations.stream()
				.filter(lineStation -> lineStation.isPreStationOf(inputLineStation))
				.findFirst()
				.orElseThrow(() -> new LineStationCreateException("연결될 수 없는 역을 입력하셨습니다."));

		if (isLastStation(preLineStation)) {
			addLineStationOnLast(inputLineStation);
			return;
		}

		int index = lineStations.indexOf(preLineStation);
		LineStation nextByInputLineStation = lineStations.get(index + 1);
		nextByInputLineStation.updatePreStationId(inputLineStation.getStationId());
		lineStations.add(index + 1, inputLineStation);
	}

	private boolean isLastStation(LineStation lineStation) {
		return lineStations.indexOf(lineStation) == lineStations.size() - 1;
	}

	public void addLineStationOnLast(LineStation lineStation) {
		lineStations.add(lineStation);
	}

	public void removeLineStationById(Long stationId) {
		if (lineStations.size() == ONE_STATION) {
			lineStations.remove(FIRST_INDEX);
			return;
		}

		removeFromMultiLineStations(stationId);
	}

	private void removeFromMultiLineStations(Long stationId) {
		LineStation targetLineStation = lineStations.stream()
				.filter(station -> station.is(stationId))
				.findFirst()
				.orElseThrow(NoLineExistException::new);

		if (isNotLastStation(targetLineStation)) {
			int index = lineStations.indexOf(targetLineStation);
			LineStation nextByTargetStation = lineStations.get(index + 1);
			nextByTargetStation.updatePreStationId(targetLineStation.getPreStationId());
		}

		lineStations.remove(targetLineStation);
	}


	private boolean isNotLastStation(LineStation lineStation) {
		return !isLastStation(lineStation);
	}

	public List<Long> getLineStationsId() {
		return lineStations.stream()
				.map(LineStation::getStationId)
				.collect(Collectors.toList());
	}

	public List<LineStation> getLineStations() {
		return lineStations;
	}
}
