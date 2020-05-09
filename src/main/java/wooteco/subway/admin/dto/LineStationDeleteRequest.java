package wooteco.subway.admin.dto;

public class LineStationDeleteRequest {
	private final Long lineId;
	private final Long stationId;

	public LineStationDeleteRequest(Long lineId, Long stationId) {
		this.lineId = lineId;
		this.stationId = stationId;
	}

	public Long getLineId() {
		return lineId;
	}

	public Long getStationId() {
		return stationId;
	}
}
