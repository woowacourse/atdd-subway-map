package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
     *     <br/>And 지하철 노선이 추가되어있다.
     *     <br/>When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     *     <br/>Then 지하철역이 노선에 추가 되었다.
     *     <br/>When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     <br/>Then 지하철역 목록을 응답 받는다.
     *     <br/>And 새로 추가한 지하철역을 목록에서 찾는다.
     *     <br/>When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     *     <br/>Then 지하철역이 노선에서 제거 되었다.
     *     <br/>When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     <br/>Then 지하철역 목록을 응답 받는다.
     *     <br/>And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @TestFactory
    Stream<DynamicTest> manageLineStation() {
        // Given 지하철역이 여러 개 추가되어있다.
        createStation("낙성대역");
        createStation("압구정로데오역");
        createStation("오리역");
        createStation("잠실역");

        // And 지하철 노선이 추가되어있다.
        createLine("1호선");

        return Stream.of(
            dynamicTest("지하철 노선 추가", () -> {
                // When 지하철 노선에 지하철역을 등록하는 요청을 한다.
                StationResponse station1 = getStations().get(1);
                LineResponse line = getLine(1L);
                addStationToLine(1L, new LineStationCreateRequest(null, station1.getId(), 1, 1));

                LineResponse updatedLine = getLine(1L);
                // Then 지하철역이 노선에 추가 되었다. (압구정로데오역)
                assertThat(updatedLine.getStations()).hasSize(line.getStations().size() + 1);

                // And 새로 추가한 지하철역을 목록에서 찾는다.
                StationResponse stationAdded = updatedLine.getStations().get(0);
                assertThat(stationAdded.getName()).isEqualTo(station1.getName());
            }),
            dynamicTest("지하철역을 노선의 마지막 지점으로 추가한다.", () -> {
                // When 지하철역을 노선의 마지막 지점으로 추가한다. (압구정로데오역 - 낙성대역)
                StationResponse station1 = getStations().get(1);
                StationResponse station0 = getStations().get(0);
                addStationToLine(1L, new LineStationCreateRequest(station1.getId(), station0.getId(), 1, 1));

                // When 지하철역 노선의 지하철역 목록 조회 요청을 한다.
                LineResponse linePushed = getLine(1L);

                // Then 노선 마지막에 지하철역이 추가되었다.
                StationResponse stationPushed = linePushed.getStations().get(linePushed.getStations().size() - 1);
                assertThat(stationPushed.getName()).isEqualTo(station0.getName());
            }),
            dynamicTest("지하철역을 노선의 시작 지점으로 추가한다.", () -> {
                // When 지하철역을 노선의 시작 지점으로 추가한다. (오리역 - 압구정로데오역 - 낙성대역)
                StationResponse station2 = getStations().get(2);
                addStationToLine(1L, new LineStationCreateRequest(null, station2.getId(), 1, 1));

                // When 지하철역 노선의 지하철역 목록 조회 요청을 한다.
                LineResponse lineUnshifted = getLine(1L);

                // Then 노선 맨 앞에 지하철역이 추가되었다.
                StationResponse unshiftedStation = lineUnshifted.getStations().get(0);
                assertThat(unshiftedStation.getName()).isEqualTo(station2.getName());
            }),
            dynamicTest("지하철역을 두 역 사이에 추가한다.", () -> {
                // When 지하철역을 두 역 사이에 추가한다. (오리역 - 압구정로데오역 - 잠실역 - 낙성대역)
                StationResponse station1 = getStations().get(1);
                StationResponse station3 = getStations().get(3);
                addStationToLine(1L, new LineStationCreateRequest(station1.getId(), station3.getId(), 1, 1));

                // When 지하철역 노선의 지하철역 목록 조회 요청을 한다.
                LineResponse lineInsertedWithinTwoStations = getLine(1L);

                // Then 두 역 사이에 지하철역이 추가되었다.
                StationResponse insertedStation = lineInsertedWithinTwoStations.getStations().get(2);
                assertThat(insertedStation.getName()).isEqualTo(station3.getName());
            }),
            dynamicTest("존재하지 않는 지하철역을 추가할 수 없다.", () -> {
                // When id가 빈 값인 지하철역을 추가한다.
                StationResponse station1 = getStations().get(1);
                addStationToLine(1L, new LineStationCreateRequest(station1.getId(), null, 1, 1));

                // Then 지하철 역이 추가되지 않는다.
                LineResponse lineWithEmptyInsertion = getLine(1L);
                assertThat(lineWithEmptyInsertion.getStations()).hasSize(4);

                // When 존재하지 않는 지하철 역을 노선에 추가하였다.
                addStationToLine(1L, new LineStationCreateRequest(station1.getId(), Long.MAX_VALUE, 1, 1));

                // Then 404 상태와 함께 지하철 역이 추가되지 않는다.
                LineResponse lineWithInvalidInsertion = getLine(1L);
                assertThat(lineWithInvalidInsertion.getStations()).hasSize(4);
            }),
            dynamicTest("이미 노선에 존재하는 두 역을 추가할 수 없다.", () -> {
                // When 이미 존재하는 두 역을 노선에 추가하였다.
                StationResponse station2 = getStations().get(2);
                StationResponse station0 = getStations().get(0);
                addStationToLine(1L, new LineStationCreateRequest(station2.getId(), station0.getId(), 1, 1));

                // Then 400 상태와 함께 지하철 역이 추가되지 않는다.
                LineResponse lineWithDuplicate = getLine(1L);
                assertThat(lineWithDuplicate.getStations()).hasSize(4);
            }),
            dynamicTest("지하철역을 노선에서 삭제한다.", () -> {
                // When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
                LineResponse line = getLine(1L);
                StationResponse station0 = getStations().get(0);
                deleteStationFromLine(line.getId(), station0.getId());

                // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
                LineResponse stationExcludedLine = getLine(1L);

                // Then 지하철역이 노선에서 제거 되었다.
                assertThat(stationExcludedLine.getStations()).hasSize(3);

                // And 제외한 지하철역이 목록에 존재하지 않는다.
                assertThat(stationExcludedLine.getStations()
                    .stream()
                    .map(StationResponse::getName))
                    .doesNotContain(station0.getName());
            })
        );
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

    private void addStationToLine(final Long lineId, final LineStationCreateRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", String.valueOf(request.getPreStationId()));
        params.put("stationId", String.valueOf(request.getStationId()));
        params.put("distance", String.valueOf(request.getDistance()));
        params.put("duration", String.valueOf(request.getDuration()));

        // @formatter:off
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
        .when()
            .post("/lines/{id}", lineId)
        .then().log().all()
            .statusCode(anyOf(
                is(HttpStatus.CREATED.value()),
                is(HttpStatus.BAD_REQUEST.value()),
                is(HttpStatus.NOT_FOUND.value())
            ));
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
