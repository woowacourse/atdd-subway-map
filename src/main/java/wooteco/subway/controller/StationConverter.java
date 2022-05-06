package wooteco.subway.controller;

import wooteco.subway.dto.info.StationInfo;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

public class StationConverter {
    static StationInfo toInfo(StationRequest stationRequest) {
        return new StationInfo(stationRequest.getName());
    }

    static StationInfo toInfo(Long id, StationRequest stationRequest) {
        return new StationInfo(id, stationRequest.getName());
    }

    static StationResponse toResponse(StationInfo stationInfo) {
        return new StationResponse(stationInfo.getId(), stationInfo.getName());
    }
}
