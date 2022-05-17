package wooteco.subway.acceptance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.service.dto.station.StationResponse;

public class StationRequestHandler {

    private final RequestHandler requestHandler = new RequestHandler();

    public ExtractableResponse<Response> findStations() {
        return requestHandler.getRequest("/stations");
    }

    public ExtractableResponse<Response> createStation(Map<String, String> params) {
        return requestHandler.postRequest("/stations", params);
    }

    public ExtractableResponse<Response> removeStation(Long lineId) {
        return requestHandler.deleteRequest("/stations/" + lineId);
    }

    public Long extractId(ExtractableResponse<Response> response) {
        return requestHandler.extractId(response, StationResponse.class).getId();
    }

    public List<Long> extractIds(ExtractableResponse<Response> response) {
        return requestHandler.extractIds(response, StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
