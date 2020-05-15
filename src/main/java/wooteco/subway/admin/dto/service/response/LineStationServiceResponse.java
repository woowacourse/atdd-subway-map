package wooteco.subway.admin.dto.service.response;

public class LineStationServiceResponse {
	private Long lineId;
	private StationServiceResponse station;

	public LineStationServiceResponse(Long lineId, StationServiceResponse station) {
		this.lineId = lineId;
		this.station = station;
	}

	public Long getLineId() {
		return lineId;
	}

	public StationServiceResponse getStation() {
		return station;
	}
}
