package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        createLine("8호선");
        createLine("2호선");

        createStation("몽촌토성");
        createStation("잠실역");
        createStation("석촌");
        createStation("잠실나루");

        List<LineResponse> lines = getLines();
        LineResponse lineEight = lines.get(0);

        List<StationResponse> stations = getStations();
        assertThat(stations.size()).isEqualTo(4);
        StationResponse mongchontoseong = stations.get(0);
        StationResponse jamsil = stations.get(1);
        StationResponse seokchon = stations.get(2);

        addStation(lineEight.getId(), null, mongchontoseong.getId());
        addStation(lineEight.getId(), mongchontoseong.getId(), jamsil.getId());
        addStation(lineEight.getId(), jamsil.getId(), seokchon.getId());

        assertThat(
            isPresentIn(getLine(lineEight.getId()), jamsil.getId())
        ).isTrue();

        removeStation(lineEight.getId(), jamsil.getId());

        assertThat(
            isPresentIn(getLine(lineEight.getId()), jamsil.getId())
        ).isFalse();
    }

    private boolean isPresentIn(LineResponse lineResponse, Long stationId) {
        return lineResponse.getStations().stream()
            .map(StationResponse::getId)
            .anyMatch(id -> id.equals(stationId));
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("title", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("backgroundColor", "white");

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

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

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

    private void addStation(Long lineId, Long preStationId, Long stationId) {
        Map<String, Long> params = new HashMap<>();
        params.put("preStationId", preStationId);
        params.put("stationId", stationId);
        params.put("distance", 10L);
        params.put("duration", 10L);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines/" + lineId + "/station/").
            then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    private void removeStation(Long lineId, Long stationId) {
        given().
            when().
            delete("/lines/" + lineId + "/station/" + stationId).
            then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    private List<StationResponse> getStations() {
        return
            given().
                when().
                get("/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    private LineResponse getLine(Long lineId) {
        return
            given().
                when().
                get("/lines/" + lineId).
                then().
                log().all().
                extract().as(LineResponse.class);
    }

    private List<LineResponse> getLines() {
        return
            given().
                when().
                get("/lines").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
    }
}
