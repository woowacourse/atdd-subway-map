package wooteco.subway.admin.dto;

public class EdgeRequest {
	private Long lineId;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public EdgeRequest() {
	}

	public EdgeRequest(Long lineId, Long preStationId, Long stationId) {
		this.lineId = lineId;
		this.preStationId = preStationId;
		this.stationId = stationId;
	}

	public EdgeRequest(Long lineId, Long preStationId, Long stationId,
			int distance, int duration) {
		this.lineId = lineId;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
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
