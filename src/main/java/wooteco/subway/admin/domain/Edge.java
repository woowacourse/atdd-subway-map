package wooteco.subway.admin.domain;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

public class Edge {
	private static final int STARTER_DEFAULT_VALUE = 0;

	@Id
	private Long id;
	@NotNull
	private Long stationId;
	@NotNull
	private Long preStationId;
	@NotNull
	private Integer distance;
	@NotNull
	private Integer duration;

	private Edge() {
	}

	public Edge(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static Edge starter(Long stationId) {
		return new Edge(stationId, stationId, STARTER_DEFAULT_VALUE, STARTER_DEFAULT_VALUE);
	}

	public void updatePreStationId(Long preStationId) {
		this.preStationId = preStationId;
	}

	public boolean equalsStationId(final Long stationId) {
		return Objects.equals(this.stationId, stationId);
	}

	public boolean isNotStartEdge() {
		return !isStartEdge();
	}

	public boolean isStartEdge() {
		return preStationId.equals(stationId);
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

	@Override
	public String toString() {
		return "Edge{" +
			"stationId=" + stationId +
			", preStationId=" + preStationId +
			", distance=" + distance +
			", duration=" + duration +
			'}';
	}
}
