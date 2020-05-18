package wooteco.subway.admin.domain;

public class LineStation {
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public LineStation(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public void updatePreStationIdToTargetsStationId(LineStation target) {
		this.preStationId = target.getStationId();
	}

	public void updatePreStationIdToTargetsPreStationId(LineStation target) {
		this.preStationId = target.getPreStationId();
	}

	public boolean hasSameStationId(LineStation lineStation) {
		return this.stationId.equals(lineStation.stationId);
	}

	public boolean hasSameStationId(Long stationId) {
		return this.stationId.equals(stationId);
	}

	public boolean isPreStationOf(LineStation lineStation) {
		return stationId.equals(lineStation.getPreStationId());
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
