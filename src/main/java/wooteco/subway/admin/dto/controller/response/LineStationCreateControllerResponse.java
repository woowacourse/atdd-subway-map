package wooteco.subway.admin.dto.controller.response;

import wooteco.subway.admin.dto.response.LineStationCreateServiceResponse;
import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;

public class LineStationCreateControllerResponse {
	private Long lineId;
	private StationCreateServiceResponse station;

	private LineStationCreateControllerResponse(Long lineId, StationCreateServiceResponse station) {
		this.lineId = lineId;
		this.station = station;
	}

	public static LineStationCreateControllerResponse of(LineStationCreateServiceResponse response) {
		return new LineStationCreateControllerResponse(response.getLineId(), response.getStation());
	}

	public Long getLineId() {
		return lineId;
	}

	public StationCreateServiceResponse getStation() {
		return station;
	}
}
