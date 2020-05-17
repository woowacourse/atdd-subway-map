package wooteco.subway.admin.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
	private Long preStationId;
	@NotNull(message = "존재하지 않은 역입니다.")
	private Long stationId;
	@Min(1)
	private int distance;
	@Min(1)
	private int duration;

	private LineStationCreateRequest() {
	}

	public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public LineStation toLineStation() {
		return new LineStation(preStationId, stationId, distance, duration);
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public void setPreStationId(Long preStationId) {
		this.preStationId = preStationId;
	}

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public int getDistance() {
		return distance;
	}

	public int getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return "LineStationCreateRequest{" +
			"preStationId=" + preStationId +
			", stationId=" + stationId +
			", distance=" + distance +
			", duration=" + duration +
			'}';
	}
}
