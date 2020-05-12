package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Edge;

public class EdgeResponse {
	private Long line;
	private Long preStationId;
	private Long stationId;
	private int distance;
	private int duration;

	public EdgeResponse(Long line, Long preStationId, Long stationId, int distance,
			int duration) {
		this.line = line;
		this.preStationId = preStationId;
		this.stationId = stationId;
		this.distance = distance;
		this.duration = duration;
	}

	public static EdgeResponse of(Edge edge) {
		return new EdgeResponse(edge.getLine(), edge.getPreStationId(),
				edge.getStationId(), edge.getDistance(), edge.getDuration());
	}

	public String getCustomId() {
		return "" + line + preStationId + stationId;
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
}
