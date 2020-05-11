package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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
     * Given 지하철 노선이 추가되어있다.
     * <p>
     * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     * Then 지하철역이 노선에 추가 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 새로 추가한 지하철역을 목록에서 찾는다.
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
        createLine("8호선");

        List<LineResponse> lines = getLines();
        LineResponse lineEight = lines.get(0);

        addStation(lineEight.getId(), null, "잠실역");
        addStation(lineEight.getId(), "잠실역", "석촌");

        List<StationResponse> stations = getStations();
        StationResponse station = stations.get(0);
        StationResponse station2 = stations.get(1);

        List<LineStation> lineStations = getLineStations(lineEight.getId());

        assertThat(lineStations.stream()
                .map(LineStation::getStationId)
                .anyMatch(id -> id.equals(station.getId()))).isTrue();

        removeStation(lineEight.getId(), station2.getId());

        List<LineStation> lineStationsAfterDelete = getLineStations(lineEight.getId());
        assertThat(lineStationsAfterDelete.stream()
                .map(LineStation::getStationId)
                .anyMatch(id -> id.equals(station2.getId()))).isFalse();
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("title", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "white");

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

    private void addStation(Long lineId, String preStationName, String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationName", preStationName);
        params.put("stationName", stationName);
        params.put("distance", String.valueOf(10));
        params.put("duration", String.valueOf(10));

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.OK.value());
    }

    private void removeStation(Long lineId, Long stationId) {
        given().
                when().
                delete("/lines/" + lineId + "/stations/" + stationId).
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

    private List<LineStation> getLineStations(Long lineId) {
        return
                given().
                        when().
                        get("/lineStations/" + lineId).
                        then().
                        log().all().
                        extract().
                        jsonPath().getList(".", LineStation.class);
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
