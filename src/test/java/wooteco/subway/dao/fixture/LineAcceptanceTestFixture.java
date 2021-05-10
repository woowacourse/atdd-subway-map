package wooteco.subway.dao.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static wooteco.subway.dao.fixture.DomainFixture.*;
import static wooteco.subway.dao.fixture.StationAcceptanceTestFixture.insertStations;

public class LineAcceptanceTestFixture {

    public static Map<String, String> createLineWithSection(List<Station> stations) {
        List<Station> sortedStations = sortStationById(stations);
        insertStations(sortedStations); // 역을 먼저 Station 테이블에 삽입함.
        return createLineRequest(LINE_COLOR, LINE_NAME, sortedStations.get(0).getId(), sortedStations.get(1).getId(), DEFAULT_DISTANCE);
    }

    // 역의 아이디로 정렬
    private static List<Station> sortStationById(List<Station> stations) {
        return stations.stream()
                .sorted(Comparator.comparing(Station::getId))
                .collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> extractResponseWhenGet(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractResponseWhenPost(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractResponseWhenPut(Map<String, String> params2) {
        return RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
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

    public static Map<String, String> createLineRequest(String color, String name, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
