package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public LineStationResponse(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static LineStationResponse of(LineStation lineStation) {
		return new LineStationResponse(lineStation.getPreStationId(), lineStation.getStationId(), lineStation.getDistance(), lineStation.getDuration());
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public Long getStationId() {
		return stationId;
	}

	public int getDistance() {
		return distance;
	}

	public int getDuration() {
		return duration;
	}
}

