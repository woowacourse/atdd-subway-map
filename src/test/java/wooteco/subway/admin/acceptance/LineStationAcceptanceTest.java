package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.LineStationsResponse;
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

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // Given 지하철역이 여러 개 추가되어있다.
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");

        // And 지하철 노선이 추가되어있다.
        createLine("신분당선");

        // When 지하철 노선에 지하철역을 등록하는 요청을 한다.
        List<StationResponse> stations = getStations();
        assertThat(stations.size()).isEqualTo(4);

        List<LineResponse> lines = getLines();
        assertThat(lines.size()).isEqualTo(1);

        // Then 지하철역이 노선에 추가 되었다.
        LineResponse line = lines.get(0);
        addLineStation(String.valueOf(line.getId()), "", String.valueOf(stations.get(0).getId()), "10", "10");
        addLineStation(String.valueOf(line.getId()), String.valueOf(stations.get(0).getId()),
                String.valueOf(stations.get(1).getId()), "10", "10");
        addLineStation(String.valueOf(line.getId()), String.valueOf(stations.get(1).getId()),
                String.valueOf(stations.get(2).getId()), "10", "10");
        addLineStation(String.valueOf(line.getId()), String.valueOf(stations.get(2).getId()),
                String.valueOf(stations.get(3).getId()), "10", "10");

        // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        List<LineStationsResponse> lineStations = getLineStations();

        // Then 지하철역 목록을 응답 받는다.
        assertThat(lineStations.size()).isEqualTo(1);
        assertThat(lineStations.get(0).getStations().size()).isEqualTo(4);

        // And 새로 추가한 지하철역을 목록에서 찾는다
        LineStationsResponse lineStationsResponse = lineStations.get(0);
        List<LineStationResponse> lineStationResponses = lineStationsResponse.getStations();
        List<Long> lineStationIds = convertToStationIds(lineStationResponses);
        assertThat(lineStationIds).contains((stations.get(0).getId()));
        assertThat(lineStationIds).contains((stations.get(1).getId()));
        assertThat(lineStationIds).contains((stations.get(2).getId()));
        assertThat(lineStationIds).contains((stations.get(3).getId()));

        // When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        // Then 지하철역이 노선에서 제거 되었다
        // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        LineStationResponse lineStationResponse = lineStationResponses.get(0);
        deleteLineStation(String.valueOf(line.getId()), String.valueOf(lineStationResponse.getId()));
        List<LineStationsResponse> lineStationsResponse2 = getLineStations();
        List<LineStationResponse> lineStations2 = lineStationsResponse2.get(0).getStations();
        assertThat(lineStations2.size()).isEqualTo(3);

        // Then 지하철역 목록을 응답 받는다.
        // And 제외한 지하철역이 목록에 존재하지 않는다.
        List<Long> lineStationIds2 = convertToStationIds(lineStations2);
        assertThat(lineStationIds2).doesNotContain(lineStationResponse.getId());
    }

    private List<Long> convertToStationIds(List<LineStationResponse> lineStationResponses) {
        return lineStationResponses.stream()
                .map(LineStationResponse::getId)
                .collect(Collectors.toList());
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-green-700");

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

    private List<StationResponse> getStations() {
        return given().
                when().
                get("/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    private List<LineResponse> getLines() {
        return given().
                when().
                get("/lines").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
    }

    private void addLineStation(String lineId, String preStationId, String stationId, String distance,
            String duration) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId);
        params.put("stationId", stationId);
        params.put("distance", distance);
        params.put("duration", duration);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<LineStationsResponse> getLineStations() {
        return given().
                when().
                get("/lines/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", LineStationsResponse.class);
    }

    private void deleteLineStation(String lineId, String stationId) {
        given().
                when().
                delete("/lines/" + lineId + "/stations/" + stationId).
                then().
                log().all();
    }
}
