package wooteco.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;

public class StationFixture {

    public static Station AStation = new Station(1L, "A");
    public static Station BStation = new Station(2L, "B");
    public static Station CStation = new Station(3L, "C");
    public static Station DStation = new Station(4L, "D");
    public static Station EStation = new Station(5L, "E");

    public static Station YStation = new Station(6L, "Y");
    public static Station ZStation = new Station(7L, "Z");

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
}
