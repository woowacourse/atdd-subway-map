package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Edge {
	@Id
	private Long id;
	private Long stationId;
	private Long preStationId;
	private int distance;
	private int duration;

	public Edge() {
	}

	public Edge(Long preStationId, Long stationId, int distance, int duration) {
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public void update(Edge edge) {
		stationId = edge.getStationId();
		preStationId = edge.getPreStationId();
		distance = edge.distance;
		duration = edge.duration;
	}

	public boolean isStationId(final Long stationId) {
		return Objects.equals(this.stationId, stationId);
	}

    public boolean isPreStationId(Long preStationOfNext) {
        return Objects.equals(this.preStationId, preStationOfNext);
    }

	public boolean isNotStartEdge() {
		return preStationId != null;
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
