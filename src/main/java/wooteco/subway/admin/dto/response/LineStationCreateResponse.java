package wooteco.subway.admin.dto.response;

import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;

public class LineStationCreateResponse {
	private Long lineId;
	private StationCreateServiceResponse station;

	public LineStationCreateResponse(Long lineId, StationCreateServiceResponse station) {
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
