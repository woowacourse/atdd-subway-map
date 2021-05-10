package wooteco.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
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
import wooteco.subway.line.LineRequest;
import wooteco.subway.line.LineResponse;
import wooteco.subway.station.StationRequest;
import wooteco.subway.station.StationResponse;

@Transactional
@Sql("classpath:test-schema.sql")
class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        System.out.println(resultLineIds.size());
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void addSection() {
        long station1 = addStation("옥수역").getId();
        long station2 = addStation("약수역").getId();

        long line = addLine("3호선", "orange", 1, 2, 10).getId();

        SectionRequest sectionRequest = new SectionRequest(station1, station2, 100);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + line + "/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertEquals(line, lineResponse.getId());
        assertEquals("3호선", lineResponse.getName());
        assertEquals("orange", lineResponse.getColor());
        assertTrue(lineResponse.getStations().containsAll(
            Arrays.asList(
                new StationResponse(station1, "옥수역"),
                new StationResponse(station2, "약수역")
            )));
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        long station1 = addStation("옥수역").getId();
        long station2 = addStation("약수역").getId();

        long line = addLine("3호선", "orange", 1, 2, 10).getId();

        SectionRequest sectionRequest = new SectionRequest(station1, station2, 100);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + line + "/sections?stationId=" + station2)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //TODO: 삭제 확
    }

    private StationResponse addStation(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        ExtractableResponse<Response> response= RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        return response.jsonPath().getObject(".", StationResponse.class);
    }

    private LineResponse addLine(String name, String color, long upStationId, long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> response= RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        return response.jsonPath().getObject(".", LineResponse.class);
    }
}