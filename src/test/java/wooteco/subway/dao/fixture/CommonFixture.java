package wooteco.subway.dao.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.Map;

public class CommonFixture {
    public static Line makeLine(String color, String name) {
        return new Line(color, name);
    }

    public static Station makeStation(Long id, String name) {
        return new Station(id, name);
    }

    public static Station makeStation(String name) {
        return new Station(name);
    }

    public static ExtractableResponse<Response> extractResponseWhenGet(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractResponseWhenPost(Map<String, String> params, String uri) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractResponseWhenPut(Map<String, String> params, String uri) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractResponseWhenDelete(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
