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
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // given - and
        List<StationResponse> stationResponses = createStations("잠실역", "잠실새내역", "종합운동장역");
        List<Long> stationIds = stationResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        LineResponse line = createLine("2호선");
        // when
        createLineStations(line.getId(), stationIds);
        // then
        getStationByLineId(line.getId());
        //when & then
        getStationsOfLine(line.getId());
        //when & then
        deleteStationOfLine(line.getId());
        //when & then
        getStationsOfLineButCanNotFind(line.getId());
    }

    private void getStationsOfLineButCanNotFind(long lineId) {
        given().
                when().
                get("/lines/" + lineId).
                then().
                log().all().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void deleteStationOfLine(long lineId) {
        given().
                when().
                delete("/lines/" + lineId).
                then().
                log().all().
                statusCode(HttpStatus.NO_CONTENT.value());

    }

    private void getStationsOfLine(long lineId) {
        given().
                when().
                get("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.OK.value());

    }

    private List<StationResponse> createStations(String... names) {
        return Stream.of(names)
                .map(this::createStation)
                .collect(Collectors.toList());
    }

    private StationResponse createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value()).
                extract().as(StationResponse.class);
    }

    private LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor","red");

        return given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value())
                .extract().as(LineResponse.class);
    }

    private void createLineStations(Long lineId, List<Long> stationIds) {
        for (int i = 0; i < stationIds.size(); i++) {
            Long preStationId = i == 0 ? null : stationIds.get(i - 1);
            createLineStation(lineId, stationIds.get(i), preStationId);
        }
    }

    private void createLineStation(Long lineId, Long stationId, Long preStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("preStationId", preStationId);
        params.put("stationId", stationId);
        params.put("distance", 10);
        params.put("duration", 10);

        given().
                body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/{lineId}/line-stations", lineId).
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private LineResponse getStationByLineId(Long id) {
        return given().
                when().
                get("/lines/" + id + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.OK.value()).
                extract().as(LineResponse.class);
    }
}
