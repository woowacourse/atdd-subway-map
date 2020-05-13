package wooteco.subway.admin.domain;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

public class Edge {
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

	public void updatePreStationId(Long preStationId) {
		this.preStationId = preStationId;
	}

	public void update(Edge edge) {
		stationId = edge.getStationId();
		preStationId = edge.getPreStationId();
		distance = edge.distance;
		duration = edge.duration;
	}

	public boolean stationIdEquals(final Long stationId) {
		return Objects.equals(this.stationId, stationId);
	}

	public boolean preStationIdEquals(Long preStationId) {
		return Objects.equals(this.preStationId, preStationId);
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
