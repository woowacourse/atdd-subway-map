package wooteco.subway.controller;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dto.info.RequestForLineService;
import wooteco.subway.dto.info.RequestToUpdateLine;
import wooteco.subway.dto.info.ResponseToLineService;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;

public class LineConverter {
    static RequestToUpdateLine toInfo(Long id, LineRequest lineRequest) {
        return new RequestToUpdateLine(id, lineRequest.getName(), lineRequest.getColor());
    }

    static RequestForLineService toInfo(LineRequest lineRequest) {
        return new RequestForLineService(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    static LineResponse toResponse(ResponseToLineService responseToLineService) {
        List<StationResponse> stationResponses = responseToLineService.getStationInfos().stream()
            .map(StationConverter::toResponse)
            .collect(Collectors.toList());
        return new LineResponse(responseToLineService.getId(), responseToLineService.getName(),
            responseToLineService.getColor(),
            stationResponses);
    }
}
