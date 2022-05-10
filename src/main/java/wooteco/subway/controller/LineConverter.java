package wooteco.subway.controller;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dto.info.LineInfo;
import wooteco.subway.dto.info.RequestLineInfo;
import wooteco.subway.dto.info.ResponseLineInfo;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.LineResponse2;
import wooteco.subway.dto.response.StationResponse;

public class LineConverter {
    static LineInfo toInfo(LineRequest lineRequest) {
        return new LineInfo(lineRequest.getName(), lineRequest.getColor());
    }

    static LineInfo toInfo(Long id, LineRequest lineRequest) {
        return new LineInfo(id, lineRequest.getName(), lineRequest.getColor());
    }

    static LineResponse toResponse(LineInfo lineInfo) {
        return new LineResponse(lineInfo.getId(), lineInfo.getName(), lineInfo.getColor());
    }

    static RequestLineInfo toInfo2(LineRequest lineRequest) {
        return new RequestLineInfo(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    static RequestLineInfo toInfo2(Long id, LineRequest lineRequest) {
        return new RequestLineInfo(id, lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    static LineResponse2 toResponse(ResponseLineInfo responseLineInfo) {
        List<StationResponse> stationResponses = responseLineInfo.getStationInfos().stream()
            .map(StationConverter::toResponse)
            .collect(Collectors.toList());
        return new LineResponse2(responseLineInfo.getId(), responseLineInfo.getName(), responseLineInfo.getColor(),
            stationResponses);
    }
}
