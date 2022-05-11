package wooteco.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;

public class StationFixture {

    public static Station stationA = new Station(1L, "A");
    public static Station stationB = new Station(2L, "B");
    public static Station stationC = new Station(3L, "C");
    public static Station stationD = new Station(4L, "D");
    public static Station stationE = new Station(5L, "E");

    public static Station stationY = new Station(6L, "Y");
    public static Station stationZ = new Station(7L, "Z");

    public static Long getSavedStationId(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static ExtractableResponse<Response> createStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
