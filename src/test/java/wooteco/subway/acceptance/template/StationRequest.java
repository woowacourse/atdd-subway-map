package wooteco.subway.acceptance.template;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.request.StationRequestDto;

import java.util.HashMap;
import java.util.Map;

public class StationRequest {
    public static ExtractableResponse<Response> createStationRequestAndReturnResponse(StationRequestDto dto) {
        Map<String, String> params = new HashMap<>();
        params.put("name", dto.getName());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static Long createStationRequestAndReturnId(StationRequestDto dto) {
        ExtractableResponse<Response> response = createStationRequestAndReturnResponse(dto);
        return response.jsonPath().getLong("id");
    }
}
