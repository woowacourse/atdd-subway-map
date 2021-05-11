package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.response.LineResponse;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestUtils {
    public static ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static List<Long> requestAndGetAllLineIds() {
        return getAllLinesInIdOrder().stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    public static List<LineResponse> getAllLinesInIdOrder() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        List<LineResponse> lineResponse = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class));
        lineResponse.sort(Comparator.comparingLong(LineResponse::getId));
        return lineResponse;
    }

    public static ExtractableResponse<Response> requestUpdateLine(Long lineIdToUpdate, String newLineName, String newColor) {
        Map<String, String> params = new HashMap<>();
        params.put("name", newLineName);
        params.put("color", newColor);

        return RestAssured.given().log().all()
                .pathParam("id", lineIdToUpdate)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static void assertLineResponseDto(LineResponse lineResponse, long lineId, String newLineName, String newColor) {
        assertThat(lineResponse.getId()).isEqualTo(lineId);
        assertThat(lineResponse.getName()).isEqualTo(newLineName);
        assertThat(lineResponse.getColor()).isEqualTo(newColor);
    }
}
