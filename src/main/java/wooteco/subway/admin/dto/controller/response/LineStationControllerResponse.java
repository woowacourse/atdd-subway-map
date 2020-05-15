package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.dto.service.response.LineStationServiceResponse;
import wooteco.subway.admin.dto.service.response.StationServiceResponse;

public class LineStationControllerResponse {
	private Long lineId;
	private StationServiceResponse station;

	private LineStationControllerResponse(Long lineId, StationServiceResponse station) {
		this.lineId = lineId;
		this.station = station;
	}

	public static LineStationControllerResponse of(LineStationServiceResponse response) {
		return new LineStationControllerResponse(response.getLineId(), response.getStation());
	}

	public Long getLineId() {
		return lineId;
	}

	public StationServiceResponse getStation() {
		return station;
	}
}
