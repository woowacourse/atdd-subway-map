package wooteco.subway.acceptance.request;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class LineAndStationRequest {
    public static Map<String, Long> createLineWithStationsAndSectionsRequest() {
        // 역 3개 추가
        ExtractableResponse<Response> stationResponse1 = StationRequest.createStationRequest(StationRequest.station1());
        ExtractableResponse<Response> stationResponse2 = StationRequest.createStationRequest(StationRequest.station2());
        ExtractableResponse<Response> stationResponse3 = StationRequest.createStationRequest(StationRequest.station3());
        long stationId1 = stationResponse1.jsonPath().getLong("id");
        long stationId2 = stationResponse2.jsonPath().getLong("id");
        long stationId3 = stationResponse3.jsonPath().getLong("id");

        // 라인 1개 추가 + 구간 1개 추가
        ExtractableResponse<Response> lineResponse = LineRequest.createLineRequest(LineRequest.line1(stationId1, stationId2));
        long lineId = lineResponse.jsonPath().getLong("id");

        // 구간 1개 추가
        LineRequest.createSectionRequest(LineRequest.section1(stationId2, stationId3), lineId);

        Map<String, Long> ids = new HashMap<>();
        ids.put("station1", stationId1);
        ids.put("station2", stationId2);
        ids.put("station3", stationId3);
        ids.put("line", lineId);
        return ids;
    }
}
