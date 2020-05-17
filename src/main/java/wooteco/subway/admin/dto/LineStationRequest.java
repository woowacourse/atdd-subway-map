package wooteco.subway.admin.dto;

public class LineStationRequest {
	private Long lineId;
	private String preStationName;
	private String stationName;

	public LineStationRequest(Long lineId, String preStationName, String stationName) {
		this.lineId = lineId;
		this.preStationName = preStationName;
		this.stationName = stationName;
	}

	public Long getLineId() {
		return lineId;
	}

	public String getPreStationName() {
		return preStationName;
	}

	public String getStationName() {
		return stationName;
	}
}
