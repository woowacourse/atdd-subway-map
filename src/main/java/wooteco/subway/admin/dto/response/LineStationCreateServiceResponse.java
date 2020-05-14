package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;

public class LineStationCreateServiceResponse {
	private Long lineId;
	private StationCreateServiceResponse station;

	public LineStationCreateServiceResponse(Long lineId, StationCreateServiceResponse station) {
		this.lineId = lineId;
		this.station = station;
	}

	public Long getLineId() {
		return lineId;
	}

	public StationCreateServiceResponse getStation() {
		return station;
	}
}
