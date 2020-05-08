package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationRequest {
	private Long lineId;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public LineStationRequest(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
		this.lineId = lineId;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public LineStation toLineStation() {
		return new LineStation(preStationId, stationId, distance, duration);
	}

	public Long getLineId() {
		return lineId;
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
