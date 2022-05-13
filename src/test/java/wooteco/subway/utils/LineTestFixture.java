package wooteco.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static wooteco.subway.utils.FixtureUtils.*;

public class LineTestFixture {

    private LineTestFixture() {
    }

    public static ExtractableResponse<Response> _7호선_및_역_생성요청() {
        ExtractableResponse<Response> 역_생성_응답_1 = post("/stations", 상도역);
        long stationId1 = extractId(역_생성_응답_1);

        ExtractableResponse<Response> 역_생성_응답_2 = post("/stations", 이수역);
        long stationId2 = extractId(역_생성_응답_2);

        Map<String, String> requestBody = new HashMap<>(_7호선);
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.replace("distance", "10");

        return post(LINE, _7호선);
    }

    public static ExtractableResponse<Response> 분당선_및_역_생성요청() {
        ExtractableResponse<Response> 역_생성_응답_1 = post("/stations", 강남구청역);
        long stationId1 = 역_생성_응답_1.jsonPath().getLong("id");

        ExtractableResponse<Response> 역_생성_응답_2 = post("/stations", 선릉역);
        long stationId2 = 역_생성_응답_2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>(신분당선);
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));

        return post(LINE, requestBody);
    }

    public static ExtractableResponse<Response> 노선_및_역_생성요청_케이스() {
        ExtractableResponse<Response> 역_생성_응답_1 = post(STATION, 상도역);
        long 상도역_ID = 역_생성_응답_1.jsonPath().getLong("id");

        ExtractableResponse<Response> 역_생성_응답_2 = post(STATION, 이수역);
        역_생성_응답_2.jsonPath().getLong("id");

        ExtractableResponse<Response> 역_생성_응답_3 = post(STATION, 강남구청역);
        
        long 강남구청역_ID = 역_생성_응답_3.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>(_7호선);
        requestBody.put("upStationId", String.valueOf(상도역_ID));
        requestBody.put("downStationId", String.valueOf(강남구청역_ID));

        return post(LINE, requestBody);
    }
}
