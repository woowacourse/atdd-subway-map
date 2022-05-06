package wooteco.subway.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

public class AcceptanceTestFixture {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getLineRequest(String name, String color) {
        try {
            return objectMapper.writeValueAsString(new LineRequest(name, color));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JsonParsing 에러가 발생하였습니다");
        }
    }

    public static String getStationRequest(String name) {
        try {
            return objectMapper.writeValueAsString(new StationRequest(name));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JsonParsing 에러가 발생하였습니다");
        }
    }

    public static ExtractableResponse<Response> insert(String body, String path) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> update(String body, String path) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then().log().all()
                .extract();
    }
}
