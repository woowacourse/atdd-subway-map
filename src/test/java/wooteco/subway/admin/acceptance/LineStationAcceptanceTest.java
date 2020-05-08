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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        createStation("잠실");
        createStation("천호");
        createStation("몽촌토성");

        createLine("8호선");

        createLineStation(1L, null, 1L);
        createLineStation(1L, 1L, 2L);
        createLineStation(1L, 2L, 3L);

        Set<Station> lineStations = getLine(1L).getStations();
        assertThat(lineStations.size()).isEqualTo(3);

        deleteLineStation(1L, 2L);

        assertThat(getLine(1L).getStations().size()).isEqualTo(2);
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
