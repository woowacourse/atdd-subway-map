package wooteco.subway.controller;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dto.info.LineInfoToUpdate;
import wooteco.subway.dto.info.RequestLineInfo;
import wooteco.subway.dto.info.ResponseLineInfo;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;

public class LineConverter {
    static LineInfoToUpdate toInfo(Long id, LineRequest lineRequest) {
        return new LineInfoToUpdate(id, lineRequest.getName(), lineRequest.getColor());
    }

    static RequestLineInfo toInfo(LineRequest lineRequest) {
        return new RequestLineInfo(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    static LineResponse toResponse(ResponseLineInfo responseLineInfo) {
        List<StationResponse> stationResponses = responseLineInfo.getStationInfos().stream()
            .map(StationConverter::toResponse)
            .collect(Collectors.toList());
        return new LineResponse(responseLineInfo.getId(), responseLineInfo.getName(), responseLineInfo.getColor(),
            stationResponses);
    }
}
