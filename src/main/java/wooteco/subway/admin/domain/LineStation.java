package wooteco.subway.admin.domain;

import java.util.Objects;

public class LineStation {
	private Long line;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public LineStation() {
	}

	public LineStation(Long line, Long preStationId, Long stationId, int distance, int duration) {
		this.line = line;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public Long getLine() {
		return line;
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

	public void updatePreLineStation(Long preStationId) {
		this.preStationId = preStationId;
	}

	public String getCustomId() {
		return "" + getLine() + getPreStationId() + getStationId();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LineStation that = (LineStation)o;
		return distance == that.distance &&
				duration == that.duration &&
				Objects.equals(line, that.line) &&
				Objects.equals(preStationId, that.preStationId) &&
				Objects.equals(stationId, that.stationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, preStationId, stationId, distance, duration);
	}

	@Override
	public String toString() {
		return "LineStation{" +
				"line=" + line +
				", preStationId=" + preStationId +
				", stationId=" + stationId +
				", distance=" + distance +
				", duration=" + duration +
				'}';
	}
}
