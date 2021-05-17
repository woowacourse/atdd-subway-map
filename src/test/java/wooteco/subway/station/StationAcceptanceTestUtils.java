package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.station.response.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTestUtils {
    public static ExtractableResponse<Response> createStationWithName(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static List<Long> requestAndGetAllStationIds() {
        return getAllStationResponse().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    public static List<StationResponse> getAllStationResponse() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        List<StationResponse> stationResponse = new ArrayList<>(response.jsonPath().getList(".", StationResponse.class));
        stationResponse.sort(Comparator.comparingLong(StationResponse::getId));
        return stationResponse;
    }
}
