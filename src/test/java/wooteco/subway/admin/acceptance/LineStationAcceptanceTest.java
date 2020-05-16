package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    /**
     *     Given 지하철역이 여러 개 추가되어있다.
     *
     *     And 지하철 노선이 추가되어있다.
     *
     *     When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     *     Then 지하철역이 노선에 추가 되었다.
     *
     *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     Then 지하철역 목록을 응답 받는다.
     *     And 새로 추가한 지하철역을 목록에서 찾는다.
     *
     *     When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     *     Then 지하철역이 노선에서 제거 되었다.
     *
     *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     Then 지하철역 목록을 응답 받는다.
     *     And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // given 지하철역이 여러 개 추가되어있다.
        createStation("낙성대역");
        createStation("압구정로데오역");
        // and 지하철 노선이 추가되어있다.
        createLine("1호선");

        // when 지하철 노선에 지하철역을 등록하는 요청을 한다.
        StationResponse naksungdae = getStations().get(0);
        StationResponse apgujeongRodeo = getStations().get(1);
        LineResponse line = getLine(1L);
        addStationToEmptyLine(naksungdae, line);
        addStationToEmptyLine(apgujeongRodeo, line);
        // then 지하철역이 노선에 추가 되었다.
        LineResponse updatedLine = getLine(1L);
        assertThat(updatedLine.getStations()).hasSize(line.getStations().size() + 2);
        // and 새로 추가한 지하철역을 목록에서 찾는다.
        assertThat(updatedLine.getStations()
            .stream()
            .map(Station::getName)
            .collect(Collectors.toList())).contains(naksungdae.getName(), apgujeongRodeo.getName());

        // when 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        deleteStationFromLine(line.getId(), naksungdae.getId());
        // when 지하철 노선의 지하철역 목록 조회 요청을 한다.
        LineResponse stationExcludedLine = getLine(1L);
        // then 지하철역이 노선에서 제거 되었다.
        assertThat(stationExcludedLine.getStations().size()).isEqualTo(1);
        // and 제외한 지하철역이 목록에 존재하지 않는다.
        assertThat(stationExcludedLine.getStations().stream().map(Station::getName)).doesNotContain(
            naksungdae.getName());
    }

    private void deleteStationFromLine(Long lineId, Long stationId) {
        // @formatter:off
        given()
        .when()
            .delete("/lines/{lineId}/stations/{stationId}", lineId, stationId)
        .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
        // @formatter:on
    }

    private LineResponse getLine(Long id) {
        // @formatter:off
        return given()
        .when()
            .get("/lines/" + id)
        .then()
            .log().all()
            .extract().as(LineResponse.class);
        // @formatter:on
    }

    private void addStationToEmptyLine(final StationResponse station, final LineResponse line) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", null);
        params.put("stationId", station.getId().toString());
        params.put("distance", "1");
        params.put("duration", "1");

        // @formatter:off
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
        .when()
            .post("/lines/{id}", line.getId())
        .then().log().all()
            .statusCode(HttpStatus.CREATED.value());
        // @formatter:on
    }

    private List<LineResponse> getLines() {
        // @formatter:off
        return given()
        .when()
            .get("/lines")
        .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", LineResponse.class);
        // @formatter:on
    }

    private List<StationResponse> getStations() {
        // @formatter:off
        return given()
        .when()
            .get("/stations")
        .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", StationResponse.class);
        // @formatter:on
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-gray-100");

        // @formatter:off
        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/lines")
        .then()
            .log().all().
            statusCode(anyOf(
                is(HttpStatus.CREATED.value()),
                is(HttpStatus.BAD_REQUEST.value()))
            );
        // @formatter:on
    }

    private void createStation(String name) {
        // @formatter:off
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/stations")
        .then()
            .log().all()
            .statusCode(anyOf(
                is(HttpStatus.CREATED.value()),
                is(HttpStatus.BAD_REQUEST.value()))
            );
        // @formatter:on
    }
}
