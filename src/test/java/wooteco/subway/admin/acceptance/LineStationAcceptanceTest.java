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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithOrderedStationsResponse;
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        // given
        createStation("마장역");
        createStation("왕십리역");
        createStation("행당역");
        createStation("몽촌토성역");
        createLine("5호선");
        List<StationResponse> stations = getStations();
        List<LineResponse> lines = getLines();

        //when
        createLineStation(lines.get(0).getId(), stations.get(0).getId(), null);
        createLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(0).getId());
        createLineStation(lines.get(0).getId(), stations.get(2).getId(), stations.get(1).getId());
        LineWithOrderedStationsResponse lineWithStations = getLineWithStations(lines.get(0).getId());
        //then
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);

        //when
        createLineStation(lines.get(0).getId(), stations.get(3).getId(), stations.get(2).getId());
        //then
        List<Station> orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        Station station = orderedStations.get(orderedStations.size() - 1);
        assertThat(station.getName()).isEqualTo("몽촌토성역");

        //when
        deleteLineStation(lines.get(0).getId(),orderedStations.get(orderedStations.size() - 1).getId());
        //then
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);

        orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        assertThat(orderedStations.stream()
                .anyMatch(orderedStation -> orderedStation.getName().equals("몽촌토성역"))
        ).isFalse();
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/station").
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
        params.put("color", "test-color");

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/line").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private void createLineStation(Long lineId, Long stationId, Long preStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId.toString());
        params.put("preStationId", preStationId);
        params.put("distance", "100");
        params.put("duration", "2");

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/lineStation/" + lineId).
        then().
                log().all().
                statusCode(HttpStatus.OK.value());
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        given().
        when().
                delete("/lineStation/" + lineId + "/" + stationId).
        then().
                log().all();
    }

    private List<StationResponse> getStations() {
        return given().
                when().
                    get("/station").
                then().
                    log().all().
                    extract().
                    jsonPath().getList(".", StationResponse.class);
    }

    private List<LineResponse> getLines() {
        return
                given().
                when().
                        get("/line").
                then().
                        log().all().
                        extract().
                        jsonPath().getList(".", LineResponse.class);
    }

    private LineWithOrderedStationsResponse getLineWithStations(Long lineId) {
        return
                given().
                when().
                       get("/lineStation/" + lineId).
                then().
                        log().all().
                        extract().
                        jsonPath().getObject(".", LineWithOrderedStationsResponse.class);
    }
}
