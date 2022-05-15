package wooteco.subway.controller;

import wooteco.subway.dto.info.StationDto;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

public class StationConverter {
    static StationDto toInfo(StationRequest stationRequest) {
        return new StationDto(stationRequest.getName());
    }

    static StationDto toInfo(Long id, StationRequest stationRequest) {
        return new StationDto(id, stationRequest.getName());
    }

    static StationResponse toResponse(StationDto stationDto) {
        return new StationResponse(stationDto.getId(), stationDto.getName());
    }
}
