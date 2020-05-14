package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.response.ValidatableResponse;
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
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.dto.res.StationResponse;

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
     * Given 지하철역이 여러 개 추가되어있다.
     * And 지하철 노선이 추가되어있다.
     * <p>
     * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     * Then 지하철역이 노선에 추가 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 새로 추가한 지하철역을 목록에서 찾는다.
     * <p>
     * When 노선의 중간에 지하철 역을 추가한다.
     * Then 설정한 순서대로 노선이 정렬되어 있다.
     * <p>
     * When 노선의 맨 앞에 지하철 역을 추가한다.
     * Then 설정한 순서대로 노선이 정렬되어 있다.
     * <p>
     * When 노선의 마지막에 지하철 역을 추가한다.
     * Then 설정한 순서대로 노선이 정렬되어 있다.
     * <p>
     * When 존재하지 않는 노선 아이디에 지하철 역 추가 요청을 한다.
     * Then 400 에러를 받는다.
     * <p>
     * When 노선에 존재하지 않는 지하철 역 추가 요청을 한다.
     * Then 400 에러를 받는다.
     * <p>
     * When 노선에 존재하지 않는 지하철 역을 전 역으로 추가 요청을 한다.
     * Then 400 에러를 받는다.
     * <p>
     * When 동일한 노선에 동일한 구간이 추가된다.
     * Then 400 에러를 받는다.
     * <p>
     * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     * Then 지하철역이 노선에서 제거 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        //given
        createStation("강남");
        createStation("서초");
        createStation("구의");
        createStation("잠실");
        createStation("삼송");
        createStation("지축");
        createLine("2호선");

        //when
        registerSuccess(1L, 1L, null, 2, 2);
        registerSuccess(1L, 2L, 1L, 2, 2);
        registerSuccess(1L, 3L, 2L, 2, 2);
        //then
        LineResponse line = getLine(1L);
        assertThat(line.getStations().size()).isEqualTo(3);

        //when
        final Station station = line.getStations().get(0);
        //then
        assertThat(station.getName()).isEqualTo("강남");

        //when
        registerSuccess(1L, 4L, 1L, 2, 2);
        //then
        LineResponse updatedLine = getLine(1L);
        assertThat(getLineStationsIds(updatedLine)).isEqualTo(Arrays.asList(1L, 4L, 2L, 3L));

        //when
        registerSuccess(1L, 5L, null, 2, 2);
        //then
        updatedLine = getLine(1L);
        assertThat(getLineStationsIds(updatedLine)).isEqualTo(Arrays.asList(5L, 1L, 4L, 2L, 3L));

        //when
        registerSuccess(1L, 6L, 3L, 2, 2);
        //then
        updatedLine = getLine(1L);
        assertThat(getLineStationsIds(updatedLine)).isEqualTo(Arrays.asList(5L, 1L, 4L, 2L, 3L, 6L));

        //when
        ValidatableResponse response = register(2L, 1L, null, 2, 2);
        //then
        response.statusCode(HttpStatus.BAD_REQUEST.value());

        //when
        response = register(1L, 1000L, null, 2, 2);
        response.statusCode(HttpStatus.BAD_REQUEST.value());

        //when
        response = register(1L, 1L, 1000L, 2, 2);
        response.statusCode(HttpStatus.BAD_REQUEST.value());

        //when
        response = register(1L, 2L, 4L, 2, 2);
        //then
        response.statusCode(HttpStatus.BAD_REQUEST.value());

        //when
        final int statusCode = deleteEdge(line.getId(), line.getStations().get(0).getId());
        //then
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());
        //then
        final LineResponse edgesAfterDelete = getLine(1L);
        assertThat(edgesAfterDelete.getStations().size()).isEqualTo(5);
    }

    private List<Long> getLineStationsIds(LineResponse line) {
        return line.getStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    private int deleteEdge(Long lineId, Long stationId) {
        return given()
                .when()
                .delete("/edges/lines/" + lineId + "/stations/" + stationId)
                .then()
                .log()
                .all()
                .extract()
                .statusCode();
    }

    private void registerSuccess(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
        register(lineId, stationId, preStationId, distance, duration).
                statusCode(HttpStatus.CREATED.value());
    }

    private void registerFail(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
        register(lineId, stationId, preStationId, distance, duration).
                statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private ValidatableResponse register(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", String.valueOf(preStationId));
        params.put("stationId", String.valueOf(stationId));
        params.put("distance", String.valueOf(distance));
        params.put("duration", String.valueOf(duration));

        return given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/edges/" + lineId).
                then().
                log().all();
    }

    private LineResponse getLine(Long id) {
        return given().when().
                get("/lines/" + id).
                then().
                log().all().
                extract().as(LineResponse.class);
    }

    private List<StationResponse> getStations() {
        return given().when()
                .get("/stations")
                .then()
                .extract()
                .jsonPath()
                .getList(".", StationResponse.class);
    }

    private void createStation(String station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private void createLine(String line) {
        Map<String, String> params = new HashMap<>();
        params.put("name", line);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }
}
