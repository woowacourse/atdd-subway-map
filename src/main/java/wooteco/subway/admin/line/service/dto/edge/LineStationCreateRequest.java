package wooteco.subway.admin.line.service.dto.edge;

import javax.validation.constraints.NotNull;

import wooteco.subway.admin.line.domain.edge.LineStation;

public class LineStationCreateRequest {

	private Long preStationId;

	@NotNull
	private Long stationId;

	private int distance;
	private int duration;

	public LineStationCreateRequest() {
	}

	public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
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

	public LineStation toLineStation() {
		return new LineStation(null, preStationId, stationId, distance, duration);
	}

}
