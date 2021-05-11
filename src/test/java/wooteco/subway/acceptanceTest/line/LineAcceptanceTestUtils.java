package wooteco.subway.acceptanceTest.line;

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
import wooteco.subway.controller.dto.response.line.LineResponseDto;

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

    public static List<Long> requestAndGetAllSavedLinesIds() {
        return getAllLinesInIdOrder().stream()
            .map(LineResponseDto::getId)
            .collect(Collectors.toList());
    }

    public static List<LineResponseDto> getAllLinesInIdOrder() {
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

        List<LineResponseDto> lineResponseDtos = new ArrayList<>(response.jsonPath().getList(".", LineResponseDto.class));
        lineResponseDtos.sort(Comparator.comparingLong(LineResponseDto::getId));
        return lineResponseDtos;
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

    public static void assertLineResponseDto(LineResponseDto lineResponseDto, long lineId, String newLineName, String newColor) {
        assertThat(lineResponseDto.getId()).isEqualTo(lineId);
        assertThat(lineResponseDto.getName()).isEqualTo(newLineName);
        assertThat(lineResponseDto.getColor()).isEqualTo(newColor);
    }
}
