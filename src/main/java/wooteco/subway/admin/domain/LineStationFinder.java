package wooteco.subway.admin.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LineStationFinder {
	private final Map<Long, LineStation> preStationIdElements;

	public LineStationFinder(Set<LineStation> lineStations) {
		Map<Long, LineStation> preStationIdElements = new HashMap<>();
		for (LineStation lineStation : lineStations) {
			preStationIdElements.put(lineStation.getPreStationId(), lineStation);
		}
		this.preStationIdElements = preStationIdElements;
	}

	public LineStation popByPreStationId(Long preStationId) {
		LineStation lineStation = preStationIdElements.get(preStationId);
		preStationIdElements.remove(preStationId);
		return lineStation;
	}

	public boolean hasMore() {
		return !this.preStationIdElements.isEmpty();
	}
}
