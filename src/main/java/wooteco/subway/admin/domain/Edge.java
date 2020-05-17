package wooteco.subway.admin.domain;

import java.util.Objects;

public class Edge {
	private Long line;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public Edge() {
	}

	public Edge(Long line, Long preStationId, Long stationId, int distance, int duration) {
		this.line = line;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public void updatePreEdge(Long preStationId) {
		this.preStationId = preStationId;
	}

	public boolean isEqualsWithStationId(Long id) {
		return Objects.equals(this.stationId, id);
	}

	public boolean isEqualsWithPreStationId(Long id) {
		return Objects.equals(this.preStationId, id);
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Edge that = (Edge)o;
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
