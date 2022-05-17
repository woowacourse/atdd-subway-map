package wooteco.subway.acceptance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.service.dto.line.LineResponse;

public class LineRequestHandler {

    private final RequestHandler requestHandler = new RequestHandler();

    public ExtractableResponse<Response> findLines() {
        return requestHandler.getRequest("/lines");
    }

    public ExtractableResponse<Response> findLine(Long lineId) {
        return requestHandler.getRequest("/lines/" + lineId);
    }

    public ExtractableResponse<Response> createLine(Map<String, String> params) {
        return requestHandler.postRequest("/lines", params);
    }

    public ExtractableResponse<Response> updateLine(Long lineId, Map<String, String> params) {
        return requestHandler.putRequest("/lines/" + lineId, params);
    }

    public ExtractableResponse<Response> appendSection(Long lineId, Map<String, String> params) {
        return requestHandler.postRequest("/lines/" + lineId + "/sections", params);
    }

    public ExtractableResponse<Response> removeStation(Long lineId, Long stationId) {
        return requestHandler.deleteRequest("/lines/" + lineId + "/sections/?stationId=" + stationId);
    }

    public ExtractableResponse<Response> removeLine(Long lineId) {
        return requestHandler.deleteRequest("/lines/" + lineId);
    }

    public Long extractId(ExtractableResponse<Response> response) {
        return requestHandler.extractId(response, LineResponse.class).getId();
    }

    public List<Long> extractIds(ExtractableResponse<Response> response) {
        return requestHandler.extractIds(response, LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
