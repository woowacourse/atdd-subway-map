package wooteco.subway.admin.dto;

import wooteco.subway.admin.dto.response.StationResponse;

public class LineStationCreateResponse {
	private Long lineId;
	private StationResponse station;

	public LineStationCreateResponse(Long lineId, StationResponse station) {
		this.lineId = lineId;
		this.station = station;
	}

	public Long getLineId() {
		return lineId;
	}

	public StationResponse getStation() {
		return station;
	}
}
