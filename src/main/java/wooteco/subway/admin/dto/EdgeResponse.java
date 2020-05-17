package wooteco.subway.admin.dto;

import java.util.Objects;

import wooteco.subway.admin.domain.Edge;

public class EdgeResponse {
	private Long lineId;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public EdgeResponse(Long lineId, Long preStationId, Long stationId, int distance,
			int duration) {
		this.lineId = lineId;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static EdgeResponse of(Edge edge) {
		return new EdgeResponse(edge.getLine(), edge.getPreStationId(),
				edge.getStationId(), edge.getDistance(), edge.getDuration());
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EdgeResponse that = (EdgeResponse)o;
		return distance == that.distance &&
				duration == that.duration &&
				Objects.equals(lineId, that.lineId) &&
				Objects.equals(preStationId, that.preStationId) &&
				Objects.equals(stationId, that.stationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lineId, preStationId, stationId, distance, duration);
	}
}
