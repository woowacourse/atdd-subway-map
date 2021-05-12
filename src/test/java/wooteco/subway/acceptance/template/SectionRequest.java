package wooteco.subway.acceptance.template;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.request.SectionRequestDto;

import java.util.HashMap;
import java.util.Map;

public class SectionRequest {
    public static ExtractableResponse<Response> createSectionRequestAndReturnResponse(SectionRequestDto dto, Long lineId) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", dto.getDownStationId().toString());
        params.put("upStationId", dto.getUpStationId().toString());
        params.put("distance", Integer.toString(dto.getDistance()));
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
