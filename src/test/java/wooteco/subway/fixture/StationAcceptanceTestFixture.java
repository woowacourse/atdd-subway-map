package wooteco.subway.fixture;

import io.restassured.RestAssured;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationAcceptanceTestFixture {

    public static void insertStation(Station station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station.getName());
        requestWhenCreateStation(params);
    }

    public static void insertStations(List<Station> stations) {
        for (Station station : stations) {
            Map<String, String> params = new HashMap<>();
            params.put("name", station.getName());
            requestWhenCreateStation(params);
        }
    }

    private static void requestWhenCreateStation(Map<String, String> params) {
        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
