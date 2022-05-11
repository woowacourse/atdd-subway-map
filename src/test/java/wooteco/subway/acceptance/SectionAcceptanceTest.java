package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionAcceptanceTest extends AcceptanceTest {

    private Long createdLineId;
    private Long createdFirstStationId;
    private Long createdSecondStationId;

    @BeforeEach
    void createLineAndStations() {
        createdLineId = createLine("2호선", "bg-red-600");
        createdFirstStationId = createStation("선릉역");
        createdSecondStationId = createStation("잠실역");
    }

    @Disabled
    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Long lineId = createdLineId;
        Long upStationId = createdFirstStationId;
        Long downStationId = createdSecondStationId;
        Integer distance = 10;

        // when
        ExtractableResponse<Response> response = createSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );

    }

    private ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("lineId", lineId.toString());
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return postRequest(params, "/lines/" + lineId + "/sections");
    }

    private Long createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        ExtractableResponse<Response> response = postRequest(params, "/lines");
        return Long.parseLong(response.header("Location").split("lines/")[1]);
    }

    private Long createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = postRequest(params, "/stations");
        return Long.parseLong(response.header("Location").split("stations/")[1]);
    }

    private ExtractableResponse<Response> postRequest(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }
}

