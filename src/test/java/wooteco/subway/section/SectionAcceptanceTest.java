package wooteco.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        addStation("옥수역");
        addStation("약수역");
        addLine("3호선", "orange", 1, 2, 10);
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        long newStationId = addStation("화정역").getId();
        SectionRequest sectionRequest = new SectionRequest(1L, newStationId, 5);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + 1 + "/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertEquals(1, lineResponse.getId());
        assertEquals("3호선", lineResponse.getName());
        assertEquals("orange", lineResponse.getColor());
        assertTrue(lineResponse.getStations().containsAll(
            Arrays.asList(
                new StationResponse(1L, "옥수역"),
                new StationResponse(2L, "약수역"),
                new StationResponse(3L, "화정역")
            )));
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        //given
        long newStationId = addStation("화정역").getId();
        SectionRequest sectionRequest = new SectionRequest(1L, newStationId, 5);
        RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + 1 + "/sections")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + 1 + "/sections?stationId=" + newStationId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private StationResponse addStation(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
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
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        return response.jsonPath().getObject(".", LineResponse.class);
    }
}