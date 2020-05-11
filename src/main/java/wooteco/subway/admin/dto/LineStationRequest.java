package wooteco.subway.admin.dto;

public class LineStationRequest {
	private Long lineId;
	private Long preStationId;
	private Long stationId;

	public LineStationRequest(Long lineId, Long preStationId, Long stationId) {
		this.lineId = lineId;
		this.preStationId = preStationId;
		this.stationId = stationId;
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
}
