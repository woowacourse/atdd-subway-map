package wooteco.subway.admin.dto.controller.request;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.view.request.LineStationCreateViewRequest;

public class LineStationCreateControllerRequest {
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	private LineStationCreateControllerRequest(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static LineStationCreateControllerRequest of(LineStationCreateViewRequest request) {
		return new LineStationCreateControllerRequest(request.getPreStationId(), request.getStationId(),
				request.getDistance(), request.getDuration());
	}

	public LineStation toLineStation() {
		return new LineStation(preStationId, stationId, distance, duration);
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
