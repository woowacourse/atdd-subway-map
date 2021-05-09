package wooteco.subway.acceptance.template;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.SectionRequestDto;

import java.util.HashMap;
import java.util.Map;

public class LineRequest {
    public static ExtractableResponse<Response> createLineRequestAndReturnResponse(LineCreateRequestDto dto) {
        Map<String, String> params = lineCreateRequestDtoConvertToMap(dto);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static Long createLineRequestAndReturnId(LineCreateRequestDto dto) {
        ExtractableResponse<Response> response = createLineRequestAndReturnResponse(dto);
        Long lineId = response.jsonPath().getLong("id");
        return lineId;
    }

    private static Map<String, String> lineCreateRequestDtoConvertToMap(LineCreateRequestDto dto) {
        Map<String, String> params = new HashMap<>();
        params.put("color", dto.getColor());
        params.put("name", dto.getName());
        params.put("upStationId", dto.getUpStationId().toString());
        params.put("downStationId", dto.getDownStationId().toString());
        params.put("distance", Integer.toString(dto.getDistance()));
        return params;
    }

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
