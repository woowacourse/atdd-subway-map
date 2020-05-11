package wooteco.subway.admin.dto;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.List;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
	private Long lineId;
	private Long stationId;
	private Long preStationId;
	private int distance;
	private int duration;

	private LineStationResponse() {
	}

	public LineStationResponse(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
		this.lineId = lineId;
		this.stationId = stationId;
		this.preStationId = preStationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static LineStationResponse of(Long lineId, LineStation lineStation) {
		return new LineStationResponse(lineId, lineStation.getStationId(),
			lineStation.getPreStationId(), lineStation.getDistance(), lineStation.getDuration());
	}

	public static List<LineStationResponse> ofList(Long lineId, List<LineStation> lineStations) {
		return lineStations.stream()
			.map(lineStation -> LineStationResponse.of(lineId, lineStation))
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	public Long getLineId() {
		return lineId;
	}

	public Long getStationId() {
		return stationId;
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public int getDistance() {
		return distance;
	}

	public int getDuration() {
		return duration;
	}
}
