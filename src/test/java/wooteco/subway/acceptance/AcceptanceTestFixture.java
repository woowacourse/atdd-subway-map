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

    public static final LineRequest.Post line1Post = new LineRequest.Post("1호선", "blue", 1L, 2L, 10);
    public static final LineRequest.Post line2Post = new LineRequest.Post("2호선", "green", 3L, 4L, 10);
    public static final LineRequest.Put line2Put = new LineRequest.Put("2호선", "blue");


    public static String getLineRequest(LineRequest.Put lineRequestPut) {
        try {
            return objectMapper.writeValueAsString(lineRequestPut);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JsonParsing 에러가 발생하였습니다");
        }
    }

    public static String getLineRequest(LineRequest.Post lineRequestPost) {
        try {
            return objectMapper.writeValueAsString(lineRequestPost);
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
