package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationRequestForm {

    private StationRequestForm() {
    }

    public static Map<String, String> stationRequestBody(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }

    public static ExtractableResponse<Response> createRequest(Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAllRequest() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteRequest(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
