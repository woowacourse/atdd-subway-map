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
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
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
        //Given
        createStation("잠실");
        createStation("천호");
        createStation("몽촌토성");
        //And
        createLine("8호선");

        //When
        createLineStation(1L, null, 1L);
        createLineStation(1L, 1L, 2L);
        createLineStation(1L, 2L, 3L);
        //Then
        Set<Station> stations = getLine(1L).getStations();
        assertThat(stations.size()).isEqualTo(3);
        assertTrue(isStationExists(stations, "잠실"));
        assertTrue(isStationExists(stations, "천호"));
        assertTrue(isStationExists(stations, "몽촌토성"));

        //When
        deleteLineStation(1L, 2L);
        //Then
        stations = getLine(1L).getStations();
        assertThat(stations.size()).isEqualTo(2);
        //And
        assertFalse(isStationExists(stations, "천호"));
    }

    private boolean isStationExists(Set<Station> stations, String stationName) {
        return stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList())
                .contains(stationName);
    }

    private void deleteLineStation(long lineId, long stationId) {
        given().when().
                delete("/lines/" + lineId + "/stations/" + stationId).
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

    private void createLineStation(Long lineId, Long preStationId, Long stationId) {
        Map<String, Long> params = new HashMap<>();
        params.put("stationId", stationId);
        params.put("preStationId", preStationId);
        params.put("distance", 2L);
        params.put("duration", 2L);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                put("/lines/" + lineId+ "/stations").
        then().
                log().all().
                statusCode(HttpStatus.OK.value());
    }

    private Map<String, String> makeLineParam(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("color", "bg-gray-700");
        return params;
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

    private void createLine(String name) {
        Map<String, String> params = makeLineParam(name);

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
