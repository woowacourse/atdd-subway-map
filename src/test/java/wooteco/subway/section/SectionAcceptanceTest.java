package wooteco.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.LineResponse;
import wooteco.subway.station.StationRequest;
import wooteco.subway.station.StationResponse;

@Transactional
@Sql("classpath:schema.sql")
class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        addStation("강남역");
        addStation("잠실역");

        addLine("2호선", "red", 1, 2, 10);
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void addSection() { //TODO: 테스트 에러
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 2);
        params.put("downStationId", 3);
        params.put("distance", 100);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertEquals(1, lineResponse.getId());
        assertEquals("2호선", lineResponse.getName());
        assertEquals("red", lineResponse.getColor());
        assertTrue(lineResponse.getStations().containsAll(Arrays.asList(new StationResponse(1L, "강남역"), new StationResponse(2L, "잠실역"))));
    }

    private void addStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> addLine(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}