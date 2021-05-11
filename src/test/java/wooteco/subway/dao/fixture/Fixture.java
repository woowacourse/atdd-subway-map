package wooteco.subway.dao.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static wooteco.subway.dao.fixture.LineAcceptanceTestFixture.createLineRequest;

public class Fixture {
    public static final Station STATION1 = new Station(1L, "부산역");
    public static final Station STATION2 = new Station(2L, "서면역");
    public static final Station STATION3 = new Station(3L, "장산역");
    public static final Station STATION4 = new Station(4L, "주례역");
    public static final List<Station> STATIONS1 = Arrays.asList(STATION1, STATION2);
    public static final List<Station> STATIONS2 = Arrays.asList(STATION3, STATION4);
    public static final List<Station> STATIONS_SAME = Arrays.asList(STATION1, STATION1);
    public static final String LINE_NAME = "1호선";
    public static final String LINE_COLOR = "bg-red-100";
    public static final int DEFAULT_DISTANCE = 7;
    public static final Map<String, String> PARAMS1 =
            createLineRequest("bg-red-600", "1호선", 1L, 2L, 7);
    public static final Map<String, String> PARAMS2 =
            createLineRequest("bg-green-600", "2호선", 1L, 2L, 7);
    public static final Map<String, String> PARAMS_INCORRECT_FORMAT =
            createLineRequest("bg-red-600", "1호쥐", 1L, 2L, 7);
    public static final Map<String, String> PARAMS_SAME_COLOR =
            createLineRequest("bg-red-600", "2호선", 1L, 2L, 7);

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
