package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("노선 조회시 역이 순서대로 조회되어야한다.")
    void showLineById() {
        String namSungStationId = setUpStations("남성역").split("/")[2];
        String isuStationId = setUpStations("이수역").split("/")[2];
        String lineId = setUpLine("7호선", namSungStationId, isuStationId, "10").split("/")[2];

        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then()
                .extract();

        // todo response에서 역들을 parsing하여 assert 문 작성

        stashLine(lineId);
        stashStations(namSungStationId);
        stashStations(isuStationId);


    }

    private String setUpStations(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .extract();

        return response.header("Location");
    }

    private String setUpLine(String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", lineName);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();

        return response.header("Location");
    }

    private void stashLine(String lineId) {
        RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then()
                .extract();
    }

    private void stashStations(String stationId) {
        RestAssured.given().log().all()
                .when()
                .delete("/stations/" + stationId)
                .then()
                .extract();
    }

    // todo 구간 추가 인수테스트 작성
}
