package wooteco.subway.acceptanceTest.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.response.station.StationResponseDto;

public class StationAcceptanceTestUtils {

    public static ExtractableResponse<Response> requestCreateStationWithNameAndGetResponse(String name) {
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

    public static List<Long> requestAndGetAllSavedStationIds() {
        return requestAndGetAllSavedStationResponseDtosInOrder().stream()
            .map(StationResponseDto::getId)
            .collect(Collectors.toList());
    }

    public static List<StationResponseDto> requestAndGetAllSavedStationResponseDtosInOrder() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        List<StationResponseDto> stationResponseDtos = new ArrayList<>(response.jsonPath().getList(".", StationResponseDto.class));
        stationResponseDtos.sort(Comparator.comparingLong(StationResponseDto::getId));
        return stationResponseDtos;
    }
}
