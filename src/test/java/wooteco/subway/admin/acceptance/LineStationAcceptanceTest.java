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
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        createStation("강변");
        createStation("잠실나루");
        createStation("잠실");

        createLine("2호선");

        List<StationResponse> stations = getStations();
        LineResponse line = getLine(1L);

        createLineStation(null, stations.get(0).getId(), line.getId());
        createLineStation(stations.get(0).getId(), stations.get(1).getId(), line.getId());
        createLineStation(stations.get(1).getId(), stations.get(2).getId(), line.getId());

        List<LineStationResponse> lineStations = getLineStations(line.getId());
        assertThat(lineStations.size()).isEqualTo(3);

        LineStationResponse lineStationResponse = lineStations.get(0);
        assertThat(lineStationResponse.getLineId()).isEqualTo(line.getId());
        assertThat(lineStationResponse.getStationId()).isEqualTo(stations.get(0).getId());
        assertThat(lineStationResponse.getPreStationId()).isNull();

        deleteLineStation(line.getId(), stations.get(0).getId());
        List<LineStationResponse> lineStationsAfterDelete = getLineStations(line.getId());
        assertThat(lineStationsAfterDelete.size()).isEqualTo(2);

        boolean isExistLineStation = lineStationsAfterDelete.stream()
            .filter(response -> response.getLineId().equals(lineStationResponse.getLineId()))
            .anyMatch(response -> response.getStationId().equals(lineStationResponse.getStationId()));
        assertThat(isExistLineStation).isFalse();
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        given().
            when().
            delete("/lines/" + lineId + "/stations/" + stationId).
            then().
            log().all();
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        Request<Map<String, String>> param = new Request<>(params);

        given().
                body(param).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        Request<Map<String, String>> param = new Request<>(params);

        given().
                body(param).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<StationResponse> getStations() {
        return given().
                when().
                get("/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    private LineResponse getLine(Long id) {
        return given().when().
                get("/lines/" + id).
                then().
                log().all().
                extract().as(LineResponse.class);
    }

    private void createLineStation(Long preStationId, Long stationId, Long lineId) {
        Map<String, String> lineStation = new HashMap<>();
        lineStation.put("lineId", Long.toString(lineId));
        if (preStationId == null) {
            lineStation.put("preStationId", null);
        } else {
            lineStation.put("preStationId", Long.toString(preStationId));
        }
        lineStation.put("stationId", Long.toString(stationId));
        lineStation.put("distance", "10");
        lineStation.put("duration", "2");
        Request<Map<String, String>> param = new Request<>(lineStation);

        given().
                body(param).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<LineStationResponse> getLineStations(Long lineId) {
        return
                given().
                        when().
                        get("/lines/" + lineId + "/stations").
                        then().
                        log().all().
                        extract().
                        jsonPath().getList(".", LineStationResponse.class);
    }
}
